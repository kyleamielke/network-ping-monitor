package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.domain.model.ReportTimeRange;
import io.thatworked.support.report.domain.port.AlertDataPort;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.report.infrastructure.dto.AlertDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter for accessing alert data from the alert service.
 */
@Component
public class AlertDataAdapter implements AlertDataPort {
    
    private final AlertServiceClient alertServiceClient;
    private final StructuredLogger logger;
    
    public AlertDataAdapter(AlertServiceClient alertServiceClient, StructuredLoggerFactory loggerFactory) {
        this.alertServiceClient = alertServiceClient;
        this.logger = loggerFactory.getLogger(AlertDataAdapter.class);
    }
    
    @Override
    public List<AlertData> getAlertsInTimeRange(ReportTimeRange timeRange) {
        try {
            logger.with("operation", "getAlertsInTimeRange")
                  .with("startDate", timeRange.getStartDate())
                  .with("endDate", timeRange.getEndDate())
                  .info("Fetching alerts from alert service");
            
            // Use existing alert service client
            org.springframework.data.domain.Page<AlertDTO> alertPage = 
                alertServiceClient.getAllAlerts(0, 1000, "timestamp", "DESC");
            
            // Filter by time range
            Instant startInstant = timeRange.getStartDate().atZone(java.time.ZoneId.systemDefault()).toInstant();
            Instant endInstant = timeRange.getEndDate().atZone(java.time.ZoneId.systemDefault()).toInstant();
            
            List<AlertData> alerts = alertPage.getContent().stream()
                .filter(alert -> alert.getTimestamp() != null)
                .filter(alert -> !alert.getTimestamp().isBefore(startInstant) && !alert.getTimestamp().isAfter(endInstant))
                .map(this::mapToAlertData)
                .collect(Collectors.toList());
            
            logger.with("operation", "getAlertsInTimeRange")
                  .with("alertCount", alerts.size())
                  .info("Successfully fetched alerts from alert service");
            
            return alerts;
            
        } catch (Exception e) {
            logger.with("operation", "getAlertsInTimeRange")
                  .error("Failed to fetch alerts from alert service", e);
            throw new RuntimeException("Failed to fetch alerts", e);
        }
    }
    
    @Override
    public List<AlertData> getDeviceAlertsInTimeRange(List<UUID> deviceIds, ReportTimeRange timeRange) {
        List<AlertData> allAlerts = getAlertsInTimeRange(timeRange);
        
        return allAlerts.stream()
                .filter(alert -> deviceIds.contains(alert.deviceId()))
                .collect(Collectors.toList());
    }
    
    private AlertData mapToAlertData(AlertDTO dto) {
        return new AlertData(
            dto.getId().toString(),
            dto.getDeviceId(),
            dto.getDeviceName(),
            mapAlertType(dto.getAlertType()),
            AlertSeverity.MEDIUM, // Default since DTO doesn't have severity
            dto.getMessage(),
            dto.getTimestamp(),
            mapAlertStatus(dto.isResolved() ? "RESOLVED" : "ACTIVE"),
            dto.getResolvedAt()
        );
    }
    
    private AlertType mapAlertType(String type) {
        return switch (type.toUpperCase()) {
            case "DEVICE_DOWN" -> AlertType.DEVICE_DOWN;
            case "DEVICE_RECOVERED" -> AlertType.DEVICE_RECOVERED;
            case "HIGH_RESPONSE_TIME" -> AlertType.HIGH_RESPONSE_TIME;
            default -> AlertType.NETWORK_TIMEOUT;
        };
    }
    
    private AlertStatus mapAlertStatus(String status) {
        return switch (status.toUpperCase()) {
            case "RESOLVED" -> AlertStatus.RESOLVED;
            case "ACKNOWLEDGED" -> AlertStatus.ACKNOWLEDGED;
            default -> AlertStatus.ACTIVE;
        };
    }
}