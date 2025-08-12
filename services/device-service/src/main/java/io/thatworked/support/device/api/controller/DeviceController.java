package io.thatworked.support.device.api.controller;

import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.model.PagedResult;
import io.thatworked.support.device.api.dto.response.DeviceDTO;
import io.thatworked.support.device.api.dto.request.DeviceCreateRequest;
import io.thatworked.support.device.api.dto.request.DeviceUpdateRequest;
import io.thatworked.support.device.api.dto.request.DeviceSearchCriteria;
import io.thatworked.support.device.api.dto.request.DeviceFilter;
import io.thatworked.support.device.api.dto.request.PageRequestDTO;
import io.thatworked.support.device.api.dto.request.BulkUpdateRequest;
import io.thatworked.support.device.api.dto.response.DeviceSearchResult;
import io.thatworked.support.device.api.dto.response.PageResponse;
import io.thatworked.support.device.api.dto.response.BulkOperationResult;
import io.thatworked.support.device.application.usecase.*;
import io.thatworked.support.device.application.service.DeviceQueryApplicationService;
import io.thatworked.support.common.exception.EntityNotFoundException;
import io.thatworked.support.device.api.mapper.DeviceMapper;
import io.thatworked.support.device.config.properties.DeviceServiceProperties;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {
    private final StructuredLogger logger;
    private final DeviceServiceProperties properties;
    private final DeviceQueryApplicationService queryService;
    private final CreateDeviceUseCase createDeviceUseCase;
    private final UpdateDeviceUseCase updateDeviceUseCase;
    private final DeleteDeviceUseCase deleteDeviceUseCase;
    private final AssignDeviceToSiteUseCase assignToSiteUseCase;
    private final DeviceMapper deviceMapper;
    
    public DeviceController(StructuredLoggerFactory loggerFactory,
                           DeviceServiceProperties properties,
                           DeviceQueryApplicationService queryService,
                           CreateDeviceUseCase createDeviceUseCase,
                           UpdateDeviceUseCase updateDeviceUseCase,
                           DeleteDeviceUseCase deleteDeviceUseCase,
                           AssignDeviceToSiteUseCase assignToSiteUseCase,
                           DeviceMapper deviceMapper) {
        this.logger = loggerFactory.getLogger(DeviceController.class);
        this.properties = properties;
        this.queryService = queryService;
        this.createDeviceUseCase = createDeviceUseCase;
        this.updateDeviceUseCase = updateDeviceUseCase;
        this.deleteDeviceUseCase = deleteDeviceUseCase;
        this.assignToSiteUseCase = assignToSiteUseCase;
        this.deviceMapper = deviceMapper;
    }

    @GetMapping
    public ResponseEntity<PageResponse<DeviceDTO>> getAllDevices(
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        PagedResult<DeviceDomain> devicePage = queryService.findAll(pageRequest.getPage(), pageRequest.getSize());
        
        // Convert to DTOs
        List<DeviceDTO> dtos = devicePage.getContent().stream()
            .map(deviceMapper::toDTO)
            .collect(Collectors.toList());
            
        PageResponse<DeviceDTO> response = PageResponse.<DeviceDTO>builder()
            .content(dtos)
            .page(devicePage.getPageNumber())
            .size(devicePage.getPageSize())
            .totalElements(devicePage.getTotalElements())
            .totalPages(devicePage.getTotalPages())
            .hasNext(devicePage.isHasNext())
            .hasPrevious(devicePage.isHasPrevious())
            .first(devicePage.getPageNumber() == 0)
            .last(!devicePage.isHasNext())
            .build();
            
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    @Deprecated
    public ResponseEntity<List<DeviceDTO>> getAllDevicesLegacy() {
        List<DeviceDomain> devices = queryService.findAll();
        return ResponseEntity.ok(devices.stream()
            .map(deviceMapper::toDTO)
            .collect(Collectors.toList()));
    }
    
    @GetMapping("/filter")
    public ResponseEntity<PageResponse<DeviceDTO>> getDevicesFiltered(
            @Valid @ModelAttribute DeviceFilter filter,
            @Valid @ModelAttribute PageRequestDTO pageRequest) {
        // Convert filter to map
        Map<String, Object> filterMap = new HashMap<>();
        if (filter.getName() != null) filterMap.put("name", filter.getName());
        if (filter.getIpAddress() != null) filterMap.put("ipAddress", filter.getIpAddress());
        if (filter.getDeviceType() != null) filterMap.put("deviceType", filter.getDeviceType());
        if (filter.getSiteId() != null) filterMap.put("siteId", filter.getSiteId());
        
        PagedResult<DeviceDomain> devicePage = queryService.findWithFilters(filterMap, pageRequest.getPage(), pageRequest.getSize());
        
        // Convert to DTOs
        List<DeviceDTO> dtos = devicePage.getContent().stream()
            .map(deviceMapper::toDTO)
            .collect(Collectors.toList());
            
        PageResponse<DeviceDTO> response = PageResponse.<DeviceDTO>builder()
            .content(dtos)
            .page(devicePage.getPageNumber())
            .size(devicePage.getPageSize())
            .totalElements(devicePage.getTotalElements())
            .totalPages(devicePage.getTotalPages())
            .hasNext(devicePage.isHasNext())
            .hasPrevious(devicePage.isHasPrevious())
            .first(devicePage.getPageNumber() == 0)
            .last(!devicePage.isHasNext())
            .build();
            
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{deviceType}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByType(@PathVariable String deviceType) {
        List<DeviceDomain> devices = queryService.findByType(deviceType);
        return ResponseEntity.ok(devices.stream()
            .map(deviceMapper::toDTO)
            .collect(Collectors.toList()));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<DeviceDTO> getDeviceByUuid(@PathVariable UUID uuid) {
        logger.with("operation", "getDeviceByUuid")
                .with("deviceUuid", uuid)
                .debug("Fetching device by UUID");
        
        return queryService.findByUuid(uuid)
            .map(deviceMapper::toDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/site/{site}")
    public ResponseEntity<List<DeviceDTO>> getDevicesBySite(@PathVariable UUID site) {
        List<DeviceDomain> devices = queryService.findBySite(site);
        return ResponseEntity.ok(devices.stream()
            .map(deviceMapper::toDTO)
            .collect(Collectors.toList()));
    }

    @GetMapping("/site/{site}/type/{deviceType}")
    public ResponseEntity<List<DeviceDTO>> getDevicesBySiteAndType(
            @PathVariable UUID site,
            @PathVariable String deviceType) {
        List<DeviceDomain> devices = queryService.findBySiteAndType(site, deviceType);
        return ResponseEntity.ok(devices.stream()
            .map(deviceMapper::toDTO)
            .collect(Collectors.toList()));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<DeviceDTO>> getUnassignedDevices() {
        List<DeviceDomain> devices = queryService.findUnassigned();
        return ResponseEntity.ok(devices.stream()
            .map(deviceMapper::toDTO)
            .collect(Collectors.toList()));
    }

    @PostMapping("/search")
    public ResponseEntity<DeviceSearchResult> searchDevices(@RequestBody @Valid DeviceSearchCriteria searchCriteria) {
        var domainResult = queryService.search(
            searchCriteria.getName(),
            searchCriteria.getIpAddress(),
            searchCriteria.getDeviceType(),
            searchCriteria.getPage() != null ? searchCriteria.getPage() : 0,
            searchCriteria.getSize() != null ? searchCriteria.getSize() : 20
        );
        
        // Convert domain result to DTO
        DeviceSearchResult result = DeviceSearchResult.builder()
            .devices(domainResult.getDevices().stream()
                .map(deviceMapper::toDTO)
                .collect(Collectors.toList()))
            .currentPage(domainResult.getCurrentPage())
            .pageSize(domainResult.getPageSize())
            .totalElements(domainResult.getTotalElements())
            .totalPages(domainResult.getTotalPages())
            .hasNext(domainResult.isHasNext())
            .hasPrevious(domainResult.isHasPrevious())
            .build();
            
        return ResponseEntity.ok(result);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<DeviceDTO>> getDevicesByIds(@RequestBody List<UUID> deviceIds) {
        logger.with("operation", "getDevicesByIds")
              .with("deviceCount", deviceIds.size())
              .debug("Fetching devices by IDs batch");
        
        // Validate input
        if (deviceIds == null || deviceIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Limit batch size to prevent performance issues
        // TECH DEBT: Increased to 1000 to support client-side pagination
        // See ServiceProperties.java for detailed scaling notes
        if (deviceIds.size() > 1000) {
            logger.with("operation", "getDevicesByIds")
                  .with("requestedCount", deviceIds.size())
                  .with("maxAllowed", 1000)
                  .warn("Batch size exceeds maximum allowed");
            return ResponseEntity.badRequest().build();
        }
        
        List<DeviceDomain> devices = queryService.findByUuids(deviceIds);
        List<DeviceDTO> deviceDTOs = devices.stream()
            .map(deviceMapper::toDTO)
            .collect(Collectors.toList());
            
        logger.with("operation", "getDevicesByIds")
              .with("requestedCount", deviceIds.size())
              .with("foundCount", deviceDTOs.size())
              .debug("Batch device retrieval complete");
        
        return ResponseEntity.ok(deviceDTOs);
    }

    @PostMapping
    public ResponseEntity<DeviceDTO> createDevice(@RequestBody @Valid DeviceCreateRequest request) {
        logger.with("operation", "createDevice")
                .with("requestName", request.getName())
                .with("requestIpAddress", request.getIpAddress())
                .debug("Processing device creation request");
        
        DeviceDomain createdDomain = createDeviceUseCase.execute(
            request.getName(),
            request.getIpAddress(),
            request.getHostname(),
            request.getMacAddress(),
            request.getType(),
            request.getDescription(),
            request.getLocation(),
            request.getOs(),
            request.getOsType(),
            request.getMake(),
            request.getModel(),
            request.getEndpointId(),
            request.getAssetTag(),
            request.getMetadata(),
            request.getRoleIds()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(deviceMapper.toDTO(createdDomain));
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<DeviceDTO> updateDevice(
            @PathVariable UUID uuid,
            @RequestBody @Valid DeviceUpdateRequest request) {
        logger.with("operation", "updateDevice")
                .with("deviceUuid", uuid)
                .debug("Processing device update request");
        
        DeviceDomain updatedDomain = updateDeviceUseCase.execute(
            uuid,
            request.getName(),
            request.getIpAddress(),
            request.getHostname(),
            request.getMacAddress(),
            request.getType(),
            request.getDescription(),
            request.getLocation(),
            request.getOs(),
            request.getOsType(),
            request.getMake(),
            request.getModel(),
            request.getEndpointId(),
            request.getAssetTag(),
            request.getMetadata(),
            request.getRoleIds(),
            request.getVersion()
        );
        return ResponseEntity.ok(deviceMapper.toDTO(updatedDomain));
    }

    @PostMapping("/{uuid}/assign/{site}")
    public ResponseEntity<DeviceDTO> assignDeviceToSite(
            @PathVariable UUID uuid,
            @PathVariable UUID site) {
        DeviceDomain domain = assignToSiteUseCase.execute(uuid, site);
        return ResponseEntity.ok(deviceMapper.toDTO(domain));
    }

    @PostMapping("/{uuid}/unassign")
    public ResponseEntity<DeviceDTO> unassignDeviceFromSite(@PathVariable UUID uuid) {
        DeviceDomain domain = assignToSiteUseCase.removeFromSite(uuid);
        return ResponseEntity.ok(deviceMapper.toDTO(domain));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID uuid) {
        logger.with("operation", "deleteDevice")
                .with("deviceUuid", uuid)
                .debug("Processing device deletion request");
        
        deleteDeviceUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<BulkOperationResult> bulkDeleteDevices(@RequestBody List<UUID> deviceIds) {
        logger.with("operation", "bulkDeleteDevices")
                .with("deviceCount", deviceIds.size())
                .debug("Processing bulk device deletion request");
        
        if (deviceIds == null || deviceIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Limit batch size to prevent performance issues
        if (deviceIds.size() > 100) {
            logger.with("operation", "bulkDeleteDevices")
                  .with("requestedCount", deviceIds.size())
                  .with("maxAllowed", 100)
                  .warn("Bulk delete size exceeds maximum allowed");
            return ResponseEntity.badRequest().build();
        }
        
        int successful = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        
        for (UUID deviceId : deviceIds) {
            try {
                deleteDeviceUseCase.execute(deviceId);
                successful++;
            } catch (Exception e) {
                failed++;
                errors.add(String.format("Device %s: %s", deviceId, e.getMessage()));
                logger.with("deviceId", deviceId)
                      .with("error", e.getMessage())
                      .error("Failed to delete device in bulk operation", e);
            }
        }
        
        BulkOperationResult result = BulkOperationResult.builder()
            .totalRequested(deviceIds.size())
            .successful(successful)
            .failed(failed)
            .errors(errors)
            .build();
            
        logger.with("operation", "bulkDeleteDevices")
              .with("successful", successful)
              .with("failed", failed)
              .info("Bulk delete operation completed");
        
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/bulk")
    public ResponseEntity<BulkOperationResult> bulkUpdateDevices(@RequestBody BulkUpdateRequest request) {
        logger.with("operation", "bulkUpdateDevices")
                .with("deviceCount", request.getDeviceIds().size())
                .with("updateFields", request.getUpdates().keySet())
                .debug("Processing bulk device update request");
        
        if (request.getDeviceIds() == null || request.getDeviceIds().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        // Limit batch size to prevent performance issues
        if (request.getDeviceIds().size() > 100) {
            logger.with("operation", "bulkUpdateDevices")
                  .with("requestedCount", request.getDeviceIds().size())
                  .with("maxAllowed", 100)
                  .warn("Bulk update size exceeds maximum allowed");
            return ResponseEntity.badRequest().build();
        }
        
        int successful = 0;
        int failed = 0;
        List<String> errors = new ArrayList<>();
        
        for (UUID deviceId : request.getDeviceIds()) {
            try {
                // First get the device to get its current version
                DeviceDomain device = queryService.findByUuid(deviceId)
                    .orElseThrow(() -> new EntityNotFoundException("Device", deviceId.toString()));
                
                // Apply updates
                updateDeviceUseCase.execute(
                    deviceId,
                    request.getUpdates().getOrDefault("name", device.getName()),
                    request.getUpdates().getOrDefault("ipAddress", device.getIpAddress()),
                    request.getUpdates().getOrDefault("hostname", device.getHostname()),
                    request.getUpdates().getOrDefault("macAddress", device.getMacAddress()),
                    request.getUpdates().getOrDefault("type", device.getType()),
                    request.getUpdates().getOrDefault("description", device.getDescription()),
                    request.getUpdates().getOrDefault("location", device.getLocation()),
                    request.getUpdates().getOrDefault("os", device.getOs()),
                    request.getUpdates().getOrDefault("osType", device.getOsType()),
                    request.getUpdates().getOrDefault("make", device.getMake()),
                    request.getUpdates().getOrDefault("model", device.getModel()),
                    request.getUpdates().getOrDefault("endpointId", device.getEndpointId()),
                    request.getUpdates().getOrDefault("assetTag", device.getAssetTag()),
                    device.getMetadata(), // Keep existing metadata
                    null,                 // Keep existing roles (null means no change)
                    device.getVersion()   // Use current version for optimistic locking
                );
                successful++;
            } catch (Exception e) {
                failed++;
                errors.add(String.format("Device %s: %s", deviceId, e.getMessage()));
                logger.with("deviceId", deviceId)
                      .with("error", e.getMessage())
                      .error("Failed to update device in bulk operation", e);
            }
        }
        
        BulkOperationResult result = BulkOperationResult.builder()
            .totalRequested(request.getDeviceIds().size())
            .successful(successful)
            .failed(failed)
            .errors(errors)
            .build();
            
        logger.with("operation", "bulkUpdateDevices")
              .with("successful", successful)
              .with("failed", failed)
              .info("Bulk update operation completed");
        
        return ResponseEntity.ok(result);
    }
}
