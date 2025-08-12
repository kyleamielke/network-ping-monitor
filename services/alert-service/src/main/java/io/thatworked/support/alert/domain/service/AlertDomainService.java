package io.thatworked.support.alert.domain.service;

import io.thatworked.support.alert.config.BusinessRulesConfig;
import io.thatworked.support.alert.config.MessagesConfig;
import io.thatworked.support.alert.domain.exception.AlertDomainException;
import io.thatworked.support.alert.domain.exception.AlertNotFoundException;
import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.port.AlertRepository;
import io.thatworked.support.alert.domain.port.DomainLogger;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain service containing core alert business logic.
 * This service orchestrates complex business operations that involve multiple entities
 * or require domain-specific logic.
 */
public class AlertDomainService {
    
    private final AlertRepository alertRepository;
    private final DomainLogger logger;
    private final BusinessRulesConfig businessRulesConfig;
    private final MessagesConfig messagesConfig;
    
    public AlertDomainService(AlertRepository alertRepository, DomainLogger logger,
                            BusinessRulesConfig businessRulesConfig, MessagesConfig messagesConfig) {
        this.alertRepository = alertRepository;
        this.logger = logger;
        this.businessRulesConfig = businessRulesConfig;
        this.messagesConfig = messagesConfig;
    }
    
    /**
     * Create a new alert with business validation.
     */
    public AlertDomain createAlert(UUID deviceId, String deviceName, AlertType alertType, String message) {
        // Business rule: Check if device already has too many active alerts
        List<AlertDomain> activeAlerts = alertRepository.findUnresolvedByDeviceId(deviceId);
        int maxActiveAlerts = businessRulesConfig.getLifecycle().getMaxActiveAlertsPerDevice();
        if (activeAlerts.size() >= maxActiveAlerts) {
            throw new AlertDomainException(
                String.format(messagesConfig.getErrors().getDeviceMaxAlerts(), maxActiveAlerts)
            );
        }
        
        // Business rule: Don't create duplicate DEVICE_DOWN alerts
        if (alertType == AlertType.DEVICE_DOWN) {
            boolean hasActiveDeviceDownAlert = activeAlerts.stream()
                .anyMatch(alert -> alert.getAlertType() == AlertType.DEVICE_DOWN);
            
            if (hasActiveDeviceDownAlert) {
                logger.logBusinessDecision(
                    "Skip duplicate DEVICE_DOWN alert",
                    java.util.Map.of("deviceId", deviceId, "alertType", alertType),
                    "Alert already exists"
                );
                return activeAlerts.stream()
                    .filter(alert -> alert.getAlertType() == AlertType.DEVICE_DOWN)
                    .findFirst()
                    .orElseThrow();
            }
        }
        
        AlertDomain alert = new AlertDomain(deviceId, deviceName, alertType, message);
        return alertRepository.save(alert);
    }
    
    /**
     * Create a new alert with additional metadata.
     */
    public AlertDomain createAlert(UUID deviceId, String deviceName, AlertType alertType, String message,
                                 String ipAddress, Integer consecutiveFailures, String failureReason) {
        // Use the same validation logic
        List<AlertDomain> activeAlerts = alertRepository.findUnresolvedByDeviceId(deviceId);
        int maxActiveAlerts = businessRulesConfig.getLifecycle().getMaxActiveAlertsPerDevice();
        if (activeAlerts.size() >= maxActiveAlerts) {
            throw new AlertDomainException(
                String.format(messagesConfig.getErrors().getDeviceMaxAlerts(), maxActiveAlerts)
            );
        }
        
        // Check for duplicate DEVICE_DOWN alerts
        if (alertType == AlertType.DEVICE_DOWN) {
            boolean hasActiveDeviceDownAlert = activeAlerts.stream()
                .anyMatch(alert -> alert.getAlertType() == AlertType.DEVICE_DOWN);
            if (hasActiveDeviceDownAlert) {
                logger.logBusinessDecision(
                    "Skip duplicate DEVICE_DOWN alert",
                    java.util.Map.of("deviceId", deviceId, "alertType", alertType),
                    "Alert already exists"
                );
                return activeAlerts.stream()
                    .filter(alert -> alert.getAlertType() == AlertType.DEVICE_DOWN)
                    .findFirst()
                    .orElseThrow();
            }
        }
        
        AlertDomain alert = new AlertDomain(deviceId, deviceName, alertType, message, ipAddress, consecutiveFailures, failureReason);
        return alertRepository.save(alert);
    }
    
    /**
     * Auto-resolve alerts when device recovers.
     */
    public List<AlertDomain> autoResolveDeviceAlerts(UUID deviceId) {
        List<AlertDomain> unresolvedAlerts = alertRepository.findUnresolvedByDeviceId(deviceId);
        
        unresolvedAlerts.stream()
            .filter(AlertDomain::canBeAutoResolved)
            .forEach(alert -> {
                alert.markResolved();
                alertRepository.save(alert);
                logger.logDomainStateChange(
                    "Alert",
                    alert.getId().toString(),
                    "unresolved",
                    "resolved",
                    java.util.Map.of("deviceId", deviceId, "autoResolved", true)
                );
            });
        
        return unresolvedAlerts;
    }
    
    /**
     * Acknowledge an alert.
     */
    public AlertDomain acknowledgeAlert(UUID alertId, String acknowledgedBy) {
        AlertDomain alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new AlertNotFoundException(String.format(messagesConfig.getErrors().getAlertNotFound(), alertId)));
        
        alert.acknowledge(acknowledgedBy);
        return alertRepository.save(alert);
    }
    
    /**
     * Manually resolve an alert.
     */
    public AlertDomain resolveAlert(UUID alertId) {
        AlertDomain alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new AlertNotFoundException(String.format(messagesConfig.getErrors().getAlertNotFound(), alertId)));
        
        alert.markResolved();
        return alertRepository.save(alert);
    }
    
    /**
     * Clean up old alerts based on retention policy.
     */
    public int cleanupOldAlerts() {
        Duration retentionPeriod = Duration.ofDays(businessRulesConfig.getLifecycle().getRetentionPeriodDays());
        Instant cutoffDate = Instant.now().minus(retentionPeriod);
        List<AlertDomain> oldAlerts = alertRepository.findByTimestampBetween(
            Instant.EPOCH, cutoffDate
        );
        
        oldAlerts.forEach(alert -> alertRepository.deleteById(alert.getId()));
        
        logger.logBusinessEvent(
            "Alert cleanup completed",
            java.util.Map.of("deletedCount", oldAlerts.size(), "cutoffDate", cutoffDate)
        );
        return oldAlerts.size();
    }
    
    /**
     * Get alert statistics.
     */
    public AlertStatistics getStatistics() {
        return new AlertStatistics(
            alertRepository.count(),
            alertRepository.countUnresolved(),
            alertRepository.countUnacknowledged()
        );
    }
    
    /**
     * Value object for alert statistics.
     */
    public static class AlertStatistics {
        private final long totalAlerts;
        private final long unresolvedAlerts;
        private final long unacknowledgedAlerts;
        
        public AlertStatistics(long totalAlerts, long unresolvedAlerts, long unacknowledgedAlerts) {
            this.totalAlerts = totalAlerts;
            this.unresolvedAlerts = unresolvedAlerts;
            this.unacknowledgedAlerts = unacknowledgedAlerts;
        }
        
        public long getTotalAlerts() {
            return totalAlerts;
        }
        
        public long getUnresolvedAlerts() {
            return unresolvedAlerts;
        }
        
        public long getUnacknowledgedAlerts() {
            return unacknowledgedAlerts;
        }
    }
}