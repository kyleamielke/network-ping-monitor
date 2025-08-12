package io.thatworked.support.ping.domain.service;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.port.PingTargetRepository;
import io.thatworked.support.ping.domain.port.DeviceClient;
import io.thatworked.support.ping.domain.port.EventPublisher;
import io.thatworked.support.ping.domain.port.DomainLogger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain service for ping target business logic.
 */
public class PingTargetDomainService {
    
    private final PingTargetRepository pingTargetRepository;
    private final DeviceClient deviceClient;
    private final EventPublisher eventPublisher;
    private final DomainLogger domainLogger;
    
    public PingTargetDomainService(PingTargetRepository pingTargetRepository,
                                  DeviceClient deviceClient,
                                  EventPublisher eventPublisher,
                                  DomainLogger domainLogger) {
        this.pingTargetRepository = pingTargetRepository;
        this.deviceClient = deviceClient;
        this.eventPublisher = eventPublisher;
        this.domainLogger = domainLogger;
    }
    
    public PingTargetDomain createPingTarget(UUID deviceId, String ipAddress, Integer pingIntervalSeconds) {
        return createPingTarget(deviceId, ipAddress, null, pingIntervalSeconds);
    }
    
    public PingTargetDomain createPingTarget(UUID deviceId, String ipAddress, String hostname, Integer pingIntervalSeconds) {
        Map<String, Object> context = new java.util.HashMap<>();
        context.put("deviceId", deviceId);
        if (ipAddress != null) context.put("ipAddress", ipAddress);
        if (hostname != null) context.put("hostname", hostname);
        context.put("pingIntervalSeconds", pingIntervalSeconds);
        
        domainLogger.logBusinessEvent("createPingTarget", context);
        
        // Check if ping target already exists
        Optional<PingTargetDomain> existingTarget = pingTargetRepository.findById(deviceId);
        if (existingTarget.isPresent()) {
            PingTargetDomain existing = existingTarget.get();
            // Update IP address, hostname and interval if they've changed
            boolean needsUpdate = false;
            PingTargetDomain updated = existing;
            
            if ((ipAddress != null && !ipAddress.equals(existing.getIpAddress())) || 
                (ipAddress == null && existing.getIpAddress() != null)) {
                updated = updated.withIpAddress(ipAddress);
                needsUpdate = true;
            }
            
            if ((hostname != null && !hostname.equals(existing.getHostname())) || 
                (hostname == null && existing.getHostname() != null)) {
                updated = updated.withHostname(hostname);
                needsUpdate = true;
            }
            
            if (!pingIntervalSeconds.equals(existing.getPingIntervalSeconds())) {
                updated = updated.withPingInterval(pingIntervalSeconds);
                needsUpdate = true;
            }
            
            if (needsUpdate) {
                PingTargetDomain saved = pingTargetRepository.save(updated);
                domainLogger.logDomainStateChange("PingTarget", deviceId.toString(), 
                    "updated", "updated", context);
                return saved;
            }
            return existing;
        }
        
        // Create new ping target
        PingTargetDomain newTarget = PingTargetDomain.create(deviceId, ipAddress, hostname, pingIntervalSeconds);
        PingTargetDomain saved = pingTargetRepository.save(newTarget);
        
        domainLogger.logDomainStateChange("PingTarget", deviceId.toString(), 
            "none", "created", context);
            
        return saved;
    }
    
    public PingTargetDomain startMonitoring(UUID deviceId) {
        PingTargetDomain target = pingTargetRepository.findById(deviceId)
            .orElseThrow(() -> new IllegalArgumentException("Ping target not found: " + deviceId));
            
        if (target.isMonitored()) {
            domainLogger.logBusinessWarning("startMonitoring", Map.of(
                "deviceId", deviceId,
                "warning", "Target already monitored"
            ));
            return target;
        }
        
        PingTargetDomain monitored = target.withMonitored(true);
        PingTargetDomain saved = pingTargetRepository.save(monitored);
        
        eventPublisher.publishPingTargetStarted(saved);
        
        Map<String, Object> logContext = new HashMap<>();
        if (target.getIpAddress() != null) {
            logContext.put("ipAddress", target.getIpAddress());
        }
        if (target.getHostname() != null) {
            logContext.put("hostname", target.getHostname());
        }
        
        domainLogger.logDomainStateChange("PingTarget", deviceId.toString(), 
            "not_monitored", "monitored", logContext);
            
        return saved;
    }
    
    public PingTargetDomain stopMonitoring(UUID deviceId) {
        PingTargetDomain target = pingTargetRepository.findById(deviceId)
            .orElseThrow(() -> new IllegalArgumentException("Ping target not found: " + deviceId));
            
        if (!target.isMonitored()) {
            domainLogger.logBusinessWarning("stopMonitoring", Map.of(
                "deviceId", deviceId,
                "warning", "Target not monitored"
            ));
            return target;
        }
        
        PingTargetDomain unmonitored = target.withMonitored(false);
        PingTargetDomain saved = pingTargetRepository.save(unmonitored);
        
        eventPublisher.publishPingTargetStopped(saved);
        
        Map<String, Object> stopLogContext = new HashMap<>();
        if (target.getIpAddress() != null) {
            stopLogContext.put("ipAddress", target.getIpAddress());
        }
        if (target.getHostname() != null) {
            stopLogContext.put("hostname", target.getHostname());
        }
        
        domainLogger.logDomainStateChange("PingTarget", deviceId.toString(), 
            "monitored", "not_monitored", stopLogContext);
            
        return saved;
    }
    
    public void deletePingTarget(UUID deviceId) {
        Optional<PingTargetDomain> target = pingTargetRepository.findById(deviceId);
        if (target.isPresent()) {
            if (target.get().isMonitored()) {
                stopMonitoring(deviceId);
            }
            pingTargetRepository.deleteById(deviceId);
            
            Map<String, Object> deleteLogContext = new HashMap<>();
            if (target.get().getIpAddress() != null) {
                deleteLogContext.put("ipAddress", target.get().getIpAddress());
            }
            if (target.get().getHostname() != null) {
                deleteLogContext.put("hostname", target.get().getHostname());
            }
            
            domainLogger.logDomainStateChange("PingTarget", deviceId.toString(), 
                "exists", "deleted", deleteLogContext);
        }
    }
    
}