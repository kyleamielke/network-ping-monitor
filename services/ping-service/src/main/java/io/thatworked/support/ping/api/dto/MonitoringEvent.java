package io.thatworked.support.ping.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringEvent {
    
    public enum EventType {
        DEVICE_DOWN,
        DEVICE_RECOVERED,
        HIGH_RESPONSE_TIME,
        PACKET_LOSS,
        MONITORING_STARTED,
        MONITORING_STOPPED,
        CUSTOM
    }
    
    public enum Source {
        PING_SERVICE,
        SNMP_SERVICE,
        WMI_SERVICE,
        CUSTOM
    }
    
    private UUID deviceId;
    private String deviceName;
    private String ipAddress;
    private EventType eventType;
    private Source source;
    private String message;
    private Instant timestamp;
    private Map<String, Object> metadata;
    
    // Monitoring-specific fields
    private Integer consecutiveFailures;
    private Integer consecutiveSuccesses;
    private Double responseTimeMs;
    private Instant lastSuccessTime;
    private Instant lastFailureTime;
    private Double packetLossPercentage;
}