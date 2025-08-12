package io.thatworked.support.ping.api.controller;

import io.thatworked.support.ping.api.mapper.PingTargetDomainMapper;
import io.thatworked.support.ping.application.PingApplicationService;
import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.api.dto.CreatePingTargetDTO;
import io.thatworked.support.ping.api.dto.PingTargetDTO;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ping/targets")
public class PingTargetController {
    
    private final PingApplicationService pingApplicationService;
    private final PingTargetDomainMapper pingTargetDomainMapper;
    private final io.thatworked.support.common.logging.StructuredLogger logger;
    
    public PingTargetController(PingApplicationService pingApplicationService,
                               PingTargetDomainMapper pingTargetDomainMapper,
                               StructuredLoggerFactory structuredLoggerFactory) {
        this.pingApplicationService = pingApplicationService;
        this.pingTargetDomainMapper = pingTargetDomainMapper;
        this.logger = structuredLoggerFactory.getLogger(PingTargetController.class);
    }

    @GetMapping
    public ResponseEntity<List<PingTargetDTO>> getAllPingTargets() {
        List<PingTargetDomain> targets = pingApplicationService.getAllPingTargets();
        List<PingTargetDTO> result = targets.stream()
                .map(pingTargetDomainMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/active")
    public ResponseEntity<List<PingTargetDTO>> getActivePingTargets() {
        List<PingTargetDomain> targets = pingApplicationService.getAllActivePingTargets();
        List<PingTargetDTO> result = targets.stream()
                .map(pingTargetDomainMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PingTargetDTO> getPingTarget(@PathVariable UUID id) {
        Optional<PingTargetDomain> target = pingApplicationService.getPingTarget(id);
        if (target.isPresent()) {
            PingTargetDTO result = pingTargetDomainMapper.toDTO(target.get());
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/batch")
    public ResponseEntity<List<PingTargetDTO>> getPingTargetsByDeviceIds(@RequestBody List<UUID> deviceIds) {
        // Validate input
        if (deviceIds == null || deviceIds.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        logger.with("operation", "getPingTargetsByDeviceIds")
              .with("deviceCount", deviceIds.size())
              .debug("Fetching ping targets by device IDs batch");
        
        // Limit batch size to prevent performance issues
        if (deviceIds.size() > 100) {
            logger.with("operation", "getPingTargetsByDeviceIds")
                  .with("requestedCount", deviceIds.size())
                  .with("maxAllowed", 100)
                  .warn("Batch size exceeds maximum allowed");
            return ResponseEntity.badRequest().build();
        }
        
        List<PingTargetDomain> targets = pingApplicationService.getPingTargetsByDeviceIds(deviceIds);
        List<PingTargetDTO> targetDTOs = targets.stream()
            .map(pingTargetDomainMapper::toDTO)
            .collect(Collectors.toList());
            
        logger.with("operation", "getPingTargetsByDeviceIds")
              .with("requestedCount", deviceIds.size())
              .with("foundCount", targetDTOs.size())
              .debug("Batch ping target retrieval complete");
        
        return ResponseEntity.ok(targetDTOs);
    }

    @PostMapping
    public ResponseEntity<PingTargetDTO> createPingTarget(@Valid @RequestBody CreatePingTargetDTO createDTO) {
        PingTargetDomain target = pingApplicationService.createPingTarget(
                createDTO.getDeviceId(),
                createDTO.getIpAddress(),
                createDTO.getHostname(),
                createDTO.getPingIntervalSeconds());
        PingTargetDTO result = pingTargetDomainMapper.toDTO(target);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<PingTargetDTO> startPinging(@PathVariable UUID id) {
        PingTargetDomain target = pingApplicationService.startPingMonitoring(id);
        PingTargetDTO result = pingTargetDomainMapper.toDTO(target);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<PingTargetDTO> stopPinging(@PathVariable UUID id) {
        PingTargetDomain target = pingApplicationService.stopPingMonitoring(id);
        PingTargetDTO result = pingTargetDomainMapper.toDTO(target);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePingTarget(@PathVariable UUID id) {
        pingApplicationService.cleanupDeviceData(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/interval")
    public ResponseEntity<PingTargetDTO> updatePingInterval(
            @PathVariable UUID id,
            @RequestBody Map<String, Integer> request) {
        Integer interval = request.get("pingIntervalSeconds");
        if (interval == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Get current target and update it
        Optional<PingTargetDomain> currentTarget = pingApplicationService.getPingTarget(id);
        if (currentTarget.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        PingTargetDomain target = pingApplicationService.createPingTarget(
            id, currentTarget.get().getIpAddress(), interval);
        PingTargetDTO result = pingTargetDomainMapper.toDTO(target);
        return ResponseEntity.ok(result);
    }
}