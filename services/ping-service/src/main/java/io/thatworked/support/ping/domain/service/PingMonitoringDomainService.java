package io.thatworked.support.ping.domain.service;

import io.thatworked.support.ping.domain.model.AlertStateDomain;
import io.thatworked.support.ping.domain.model.PingResultDomain;
import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.port.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain service for ping monitoring business logic.
 */
public class PingMonitoringDomainService {
    
    private final PingResultRepository pingResultRepository;
    private final AlertStateRepository alertStateRepository;
    private final PingTargetRepository pingTargetRepository;
    private final DeviceClient deviceClient;
    private final EventPublisher eventPublisher;
    private final DomainLogger domainLogger;
    
    // Business configuration
    private final int alertFailureThreshold;
    private final int alertRecoveryThreshold;
    
    public PingMonitoringDomainService(PingResultRepository pingResultRepository,
                                     AlertStateRepository alertStateRepository,
                                     PingTargetRepository pingTargetRepository,
                                     DeviceClient deviceClient,
                                     EventPublisher eventPublisher,
                                     DomainLogger domainLogger,
                                     int alertFailureThreshold,
                                     int alertRecoveryThreshold) {
        this.pingResultRepository = pingResultRepository;
        this.alertStateRepository = alertStateRepository;
        this.pingTargetRepository = pingTargetRepository;
        this.deviceClient = deviceClient;
        this.eventPublisher = eventPublisher;
        this.domainLogger = domainLogger;
        this.alertFailureThreshold = alertFailureThreshold;
        this.alertRecoveryThreshold = alertRecoveryThreshold;
    }
    
    public void processPingResult(PingResultDomain pingResult) {
        domainLogger.logBusinessEvent("processPingResult", Map.of(
            "deviceId", pingResult.getDeviceId(),
            "success", pingResult.isSuccess(),
            "responseTime", pingResult.getResponseTime()
        ));
        
        // Save ping result
        pingResultRepository.save(pingResult);
        eventPublisher.publishPingResult(pingResult);
        
        // Update alert state
        updateAlertState(pingResult);
    }
    
    private void updateAlertState(PingResultDomain pingResult) {
        UUID deviceId = pingResult.getDeviceId();
        
        // Get or create alert state
        AlertStateDomain alertState = alertStateRepository.findById(deviceId)
            .orElse(AlertStateDomain.createNew(deviceId));
        
        AlertStateDomain updatedState;
        if (pingResult.isSuccess()) {
            updatedState = alertState.recordSuccess();
            
            // Check if we should resolve an alert
            if (updatedState.shouldResolveAlert(alertRecoveryThreshold)) {
                updatedState = updatedState.deactivateAlert();
                publishDeviceRecoveredEvent(deviceId, pingResult.getIpAddress());
            }
        } else {
            updatedState = alertState.recordFailure();
            
            // Check if we should trigger an alert
            if (updatedState.shouldTriggerAlert(alertFailureThreshold)) {
                updatedState = updatedState.activateAlert();
                publishDeviceDownEvent(deviceId, pingResult.getIpAddress());
            }
        }
        
        alertStateRepository.save(updatedState);
        
        domainLogger.logDomainStateChange("AlertState", deviceId.toString(),
            String.valueOf(alertState.getConsecutiveFailures()),
            String.valueOf(updatedState.getConsecutiveFailures()),
            Map.of("alertActive", updatedState.isAlertActive()));
    }
    
    private void publishDeviceDownEvent(UUID deviceId, String ipAddress) {
        Optional<DeviceClient.DeviceInfo> deviceInfo = deviceClient.findById(deviceId);
        String deviceName = deviceInfo.map(DeviceClient.DeviceInfo::name)
            .orElse("Unknown Device");
        
        eventPublisher.publishDeviceDown(deviceId, deviceName, ipAddress);
        
        domainLogger.logBusinessEvent("deviceDown", Map.of(
            "deviceId", deviceId,
            "deviceName", deviceName,
            "ipAddress", ipAddress
        ));
    }
    
    private void publishDeviceRecoveredEvent(UUID deviceId, String ipAddress) {
        Optional<DeviceClient.DeviceInfo> deviceInfo = deviceClient.findById(deviceId);
        String deviceName = deviceInfo.map(DeviceClient.DeviceInfo::name)
            .orElse("Unknown Device");
        
        eventPublisher.publishDeviceRecovered(deviceId, deviceName, ipAddress);
        
        domainLogger.logBusinessEvent("deviceRecovered", Map.of(
            "deviceId", deviceId,
            "deviceName", deviceName,
            "ipAddress", ipAddress
        ));
    }
    
    public void cleanupDeviceData(UUID deviceId) {
        domainLogger.logBusinessEvent("cleanupDeviceData", Map.of("deviceId", deviceId));
        
        // Remove ping results
        pingResultRepository.deleteByDeviceId(deviceId);
        
        // Remove alert state
        alertStateRepository.deleteById(deviceId);
        
        domainLogger.logDomainStateChange("DeviceData", deviceId.toString(),
            "exists", "deleted", Map.of("operation", "cleanup"));
    }
}