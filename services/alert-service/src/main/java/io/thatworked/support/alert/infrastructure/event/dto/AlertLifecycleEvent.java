package io.thatworked.support.alert.infrastructure.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when alert lifecycle changes occur.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertLifecycleEvent {
    
    public enum EventType {
        ALERT_CREATED,
        ALERT_ACKNOWLEDGED,
        ALERT_RESOLVED,
        ALERT_DELETED
    }
    
    private EventType eventType;
    private UUID id;
    private UUID deviceId;
    private String deviceName;
    private String alertType;
    private String message;
    private Instant timestamp;
    private String acknowledgedBy;
    private Instant eventTimestamp;
    private String ipAddress;
    private Integer consecutiveFailures;
    private String failureReason;
}