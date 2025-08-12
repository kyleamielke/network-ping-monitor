package io.thatworked.support.alert.infrastructure.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Event received from monitoring services (like ping-service).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringEvent {
    
    public enum EventType {
        DEVICE_UP,
        DEVICE_DOWN,
        DEVICE_RECOVERED,
        HIGH_LATENCY,
        PACKET_LOSS
    }
    
    public enum Source {
        PING_SERVICE,
        DASHBOARD_CACHE_SERVICE
    }
    
    private UUID deviceId;
    private String deviceName;
    private String ipAddress;
    private EventType eventType;
    private String message;
    private Instant timestamp;
    private Source source;
    
    // Additional metrics
    private Double responseTime;
    private Double packetLoss;
    
    // Extended monitoring data from ping service
    private Integer consecutiveFailures;
    private Integer consecutiveSuccesses;
    private Double responseTimeMs;
    private Instant lastSuccessTime;
    private Instant lastFailureTime;
    private Double packetLossPercentage;
    private Object metadata;
}