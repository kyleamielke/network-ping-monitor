package io.thatworked.support.ping.infrastructure.event.alert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeviceDownEvent extends DeviceAlertEvent {
    private int consecutiveFailures;
    private Instant lastSuccessTime;
    private String failureReason;
    
    public DeviceDownEvent(UUID deviceId, String deviceName, String ipAddress, 
                          int consecutiveFailures, Instant lastSuccessTime, String failureReason) {
        super(deviceId, deviceName, ipAddress);
        this.consecutiveFailures = consecutiveFailures;
        this.lastSuccessTime = lastSuccessTime;
        this.failureReason = failureReason;
    }
}