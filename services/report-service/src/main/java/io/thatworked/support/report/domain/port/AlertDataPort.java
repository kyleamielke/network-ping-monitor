package io.thatworked.support.report.domain.port;

import io.thatworked.support.report.domain.model.ReportTimeRange;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Port for accessing alert data from external services.
 */
public interface AlertDataPort {
    
    /**
     * Retrieves all alerts within the specified time range.
     */
    List<AlertData> getAlertsInTimeRange(ReportTimeRange timeRange);
    
    /**
     * Retrieves alerts for specific devices within the time range.
     */
    List<AlertData> getDeviceAlertsInTimeRange(List<UUID> deviceIds, ReportTimeRange timeRange);
    
    /**
     * Data transfer object for alert information.
     */
    record AlertData(
        String alertId,
        UUID deviceId,
        String deviceName,
        AlertType alertType,
        AlertSeverity severity,
        String message,
        Instant timestamp,
        AlertStatus status,
        Instant resolvedAt
    ) {}
    
    /**
     * Enumeration of alert types.
     */
    enum AlertType {
        DEVICE_DOWN,
        DEVICE_RECOVERED,
        HIGH_RESPONSE_TIME,
        NETWORK_TIMEOUT
    }
    
    /**
     * Enumeration of alert severities.
     */
    enum AlertSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    /**
     * Enumeration of alert statuses.
     */
    enum AlertStatus {
        ACTIVE,
        RESOLVED,
        ACKNOWLEDGED
    }
}