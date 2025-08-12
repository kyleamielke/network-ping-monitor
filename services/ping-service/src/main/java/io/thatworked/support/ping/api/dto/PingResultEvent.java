package io.thatworked.support.ping.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Standardized event for individual ping results.
 * Published to ping-results topic for real-time monitoring.
 * This is different from MonitoringEvent which represents state changes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PingResultEvent {
    
    // Event metadata
    private UUID eventId;
    private Instant timestamp;
    private String source;
    
    // Device information
    private UUID deviceId;
    private String deviceName;
    private String ipAddress;
    
    // Ping result
    private boolean success;
    private Double responseTimeMs;
    private String errorMessage;
    
    // Context
    private Integer pingIntervalSeconds;
    private Integer attemptNumber;
    
    public static PingResultEvent fromPingResult(PingResultDTO result, String deviceName, String ipAddress) {
        return PingResultEvent.builder()
            .eventId(UUID.randomUUID())
            .timestamp(result.getTimestamp())
            .source("ping-service")
            .deviceId(UUID.fromString(result.getDeviceId()))
            .deviceName(deviceName)
            .ipAddress(ipAddress)
            .success(result.isSuccess())
            .responseTimeMs(result.getResponseTimeMs() != null ? result.getResponseTimeMs().doubleValue() : null)
            .errorMessage(result.getErrorMessage())
            .build();
    }
}