package io.thatworked.support.alert.api.controller;

import io.thatworked.support.alert.api.dto.request.AlertSearchCriteria;
import io.thatworked.support.alert.api.dto.request.CreateAlertRequest;
import io.thatworked.support.alert.api.dto.response.AlertDTO;
import io.thatworked.support.alert.api.mapper.AlertDtoMapper;
import io.thatworked.support.alert.application.service.AlertQueryApplicationService;
import io.thatworked.support.alert.application.usecase.*;
import io.thatworked.support.alert.config.MessagesConfig;
import io.thatworked.support.alert.config.ServiceConfig;
import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.service.AlertDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${alert-service.service.api.base-path:/api/v1/alerts}")
@CrossOrigin(origins = "${alert-service.service.api.cors.allowed-origins:*}")
public class AlertController {
    
    private final StructuredLogger logger;
    private final CreateAlertUseCase createAlertUseCase;
    private final AcknowledgeAlertUseCase acknowledgeAlertUseCase;
    private final ResolveAlertUseCase resolveAlertUseCase;
    private final DeleteAlertUseCase deleteAlertUseCase;
    private final AlertQueryApplicationService queryService;
    private final AlertDtoMapper alertMapper;
    private final ServiceConfig serviceConfig;
    private final MessagesConfig messagesConfig;
    
    public AlertController(StructuredLoggerFactory loggerFactory,
                          CreateAlertUseCase createAlertUseCase,
                          AcknowledgeAlertUseCase acknowledgeAlertUseCase,
                          ResolveAlertUseCase resolveAlertUseCase,
                          DeleteAlertUseCase deleteAlertUseCase,
                          AlertQueryApplicationService queryService,
                          AlertDtoMapper alertMapper,
                          ServiceConfig serviceConfig,
                          MessagesConfig messagesConfig) {
        this.logger = loggerFactory.getLogger(AlertController.class);
        this.createAlertUseCase = createAlertUseCase;
        this.acknowledgeAlertUseCase = acknowledgeAlertUseCase;
        this.resolveAlertUseCase = resolveAlertUseCase;
        this.deleteAlertUseCase = deleteAlertUseCase;
        this.queryService = queryService;
        this.alertMapper = alertMapper;
        this.serviceConfig = serviceConfig;
        this.messagesConfig = messagesConfig;
    }
    
    // Create new alert
    @PostMapping
    public ResponseEntity<AlertDTO> createAlert(@Valid @RequestBody CreateAlertRequest request) {
        logger.with("operation", "createAlert")
              .with("deviceId", request.getDeviceId())
              .debug("Creating alert");
        
        AlertDomain alert = createAlertUseCase.execute(
            request.getDeviceId(),
            request.getDeviceName(),
            request.getAlertType().name(),
            request.getMessage()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(alertMapper.toDTO(alert));
    }
    
    // Get alert by ID
    @GetMapping("/{id}")
    public ResponseEntity<AlertDTO> getAlert(@PathVariable UUID id) {
        logger.with("operation", "getAlert")
              .with("alertId", id)
              .debug("Fetching alert");
        
        return queryService.findById(id)
            .map(alertMapper::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Get all alerts with pagination
    @GetMapping
    public ResponseEntity<Page<AlertDTO>> getAllAlerts(
            @RequestParam(defaultValue = "#{@serviceConfig.api.pagination.defaultPage}") int page,
            @RequestParam(defaultValue = "#{@serviceConfig.api.pagination.defaultPageSize}") int size,
            @RequestParam(defaultValue = "#{@serviceConfig.api.pagination.defaultSortField}") String sortBy,
            @RequestParam(defaultValue = "#{@serviceConfig.api.pagination.defaultSortDirection}") String sortDirection) {
        
        logger.with("operation", "getAllAlerts")
              .with("page", page)
              .with("size", size)
              .debug("Fetching all alerts");
        
        // For now, return all alerts in a page format
        // In a full implementation, we'd add pagination support to the query service
        List<AlertDomain> allAlerts = queryService.findByTimestampBetween(
            Instant.EPOCH, 
            Instant.now()
        );
        
        List<AlertDTO> dtos = allAlerts.stream()
            .map(alertMapper::toDTO)
            .collect(Collectors.toList());
        
        // Simple pagination logic
        int start = page * size;
        int end = Math.min(start + size, dtos.size());
        List<AlertDTO> pageContent = dtos.subList(start, Math.min(end, dtos.size()));
        
        Page<AlertDTO> alertPage = new PageImpl<>(
            pageContent,
            PageRequest.of(page, size, Sort.by(sortDirection.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy)),
            dtos.size()
        );
        
        return ResponseEntity.ok(alertPage);
    }
    
    // Search alerts
    @PostMapping("/search")
    public ResponseEntity<Page<AlertDTO>> searchAlerts(@Valid @RequestBody AlertSearchCriteria criteria) {
        logger.with("operation", "searchAlerts")
              .with("criteria", criteria)
              .debug("Searching alerts");
        
        // Filter based on criteria
        List<AlertDomain> filteredAlerts;
        
        if (criteria.getDeviceId() != null) {
            filteredAlerts = criteria.getIsResolved() != null && !criteria.getIsResolved() 
                ? queryService.findUnresolvedByDeviceId(criteria.getDeviceId())
                : queryService.findByDeviceId(criteria.getDeviceId());
        } else if (criteria.getAlertType() != null) {
            filteredAlerts = queryService.findByType(AlertType.valueOf(criteria.getAlertType().name()));
        } else if (criteria.getStartTime() != null || criteria.getEndTime() != null) {
            Instant start = criteria.getStartTime() != null ? criteria.getStartTime() : Instant.EPOCH;
            Instant end = criteria.getEndTime() != null ? criteria.getEndTime() : Instant.now();
            filteredAlerts = queryService.findByTimestampBetween(start, end);
        } else if (criteria.getIsAcknowledged() != null && !criteria.getIsAcknowledged()) {
            filteredAlerts = queryService.findUnacknowledged();
        } else {
            filteredAlerts = queryService.findByTimestampBetween(Instant.EPOCH, Instant.now());
        }
        
        // Apply additional filters
        if (criteria.getIsResolved() != null) {
            filteredAlerts = filteredAlerts.stream()
                .filter(alert -> alert.isResolved() == criteria.getIsResolved())
                .collect(Collectors.toList());
        }
        
        if (criteria.getIsAcknowledged() != null) {
            filteredAlerts = filteredAlerts.stream()
                .filter(alert -> alert.isAcknowledged() == criteria.getIsAcknowledged())
                .collect(Collectors.toList());
        }
        
        // Convert to DTOs and paginate
        List<AlertDTO> dtos = filteredAlerts.stream()
            .map(alertMapper::toDTO)
            .collect(Collectors.toList());
        
        int start = criteria.getPage() * criteria.getSize();
        int end = Math.min(start + criteria.getSize(), dtos.size());
        List<AlertDTO> pageContent = dtos.subList(start, Math.min(end, dtos.size()));
        
        Page<AlertDTO> alertPage = new PageImpl<>(
            pageContent,
            PageRequest.of(criteria.getPage(), criteria.getSize()),
            dtos.size()
        );
        
        return ResponseEntity.ok(alertPage);
    }
    
    // Get alerts by device
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<AlertDTO>> getAlertsByDevice(@PathVariable UUID deviceId) {
        logger.with("operation", "getAlertsByDevice")
              .with("deviceId", deviceId)
              .debug("Fetching alerts for device");
        
        List<AlertDTO> alerts = queryService.findByDeviceId(deviceId).stream()
            .map(alertMapper::toDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(alerts);
    }

    // Get alerts by multiple device IDs (batch operation)
    @PostMapping("/batch")
    public ResponseEntity<List<AlertDTO>> getAlertsByDeviceIds(@RequestBody List<UUID> deviceIds) {
        logger.with("operation", "getAlertsByDeviceIds")
              .with("deviceCount", deviceIds.size())
              .debug("Fetching alerts by device IDs batch");
        
        // Validate input
        if (deviceIds == null || deviceIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Limit batch size to prevent performance issues
        if (deviceIds.size() > 100) {
            logger.with("operation", "getAlertsByDeviceIds")
                  .with("requestedCount", deviceIds.size())
                  .with("maxAllowed", 100)
                  .warn("Batch size exceeds maximum allowed");
            return ResponseEntity.badRequest().build();
        }
        
        List<AlertDomain> alerts = queryService.findByDeviceIds(deviceIds);
        List<AlertDTO> alertDTOs = alerts.stream()
            .map(alertMapper::toDTO)
            .collect(Collectors.toList());
            
        logger.with("operation", "getAlertsByDeviceIds")
              .with("requestedCount", deviceIds.size())
              .with("foundCount", alertDTOs.size())
              .debug("Batch alert retrieval complete");
        
        return ResponseEntity.ok(alertDTOs);
    }
    
    // Get unresolved alerts by device
    @GetMapping("/device/{deviceId}/unresolved")
    public ResponseEntity<List<AlertDTO>> getUnresolvedAlertsByDevice(@PathVariable UUID deviceId) {
        logger.with("operation", "getUnresolvedAlertsByDevice")
              .with("deviceId", deviceId)
              .debug("Fetching unresolved alerts for device");
        
        List<AlertDTO> alerts = queryService.findUnresolvedByDeviceId(deviceId).stream()
            .map(alertMapper::toDTO)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(alerts);
    }
    
    // Acknowledge alert
    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<AlertDTO> acknowledgeAlert(
            @PathVariable UUID id,
            @RequestParam(required = false, defaultValue = "#{@messagesConfig.defaults.acknowledgedBy}") String acknowledgedBy) {
        
        logger.with("operation", "acknowledgeAlert")
              .with("alertId", id)
              .with("acknowledgedBy", acknowledgedBy)
              .debug("Acknowledging alert");
        
        AlertDomain alert = acknowledgeAlertUseCase.execute(id, acknowledgedBy);
        return ResponseEntity.ok(alertMapper.toDTO(alert));
    }
    
    // Resolve alert
    @PutMapping("/{id}/resolve")
    public ResponseEntity<AlertDTO> resolveAlert(@PathVariable UUID id) {
        logger.with("operation", "resolveAlert")
              .with("alertId", id)
              .debug("Resolving alert");
        
        AlertDomain alert = resolveAlertUseCase.execute(id);
        return ResponseEntity.ok(alertMapper.toDTO(alert));
    }
    
    // Delete alert
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        logger.with("operation", "deleteAlert")
              .with("alertId", id)
              .debug("Deleting alert");
        
        deleteAlertUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
    
    // Get statistics
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.with("operation", "getStatistics")
              .debug("Fetching alert statistics");
        
        AlertDomainService.AlertStatistics stats = queryService.getStatistics();
        
        Map<String, Object> response = Map.of(
            "totalAlerts", stats.getTotalAlerts(),
            "unresolvedAlerts", stats.getUnresolvedAlerts(),
            "unacknowledgedAlerts", stats.getUnacknowledgedAlerts()
        );
        
        return ResponseEntity.ok(response);
    }
}