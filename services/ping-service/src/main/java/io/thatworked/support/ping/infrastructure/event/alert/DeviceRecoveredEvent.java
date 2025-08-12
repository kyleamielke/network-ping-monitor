package io.thatworked.support.ping.infrastructure.event.alert;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeviceRecoveredEvent extends DeviceAlertEvent {
    private Instant downSince;
    private Duration downtimeDuration;
    private double currentResponseTimeMs;
    private int consecutiveSuccesses;
    
    public DeviceRecoveredEvent(UUID deviceId, String deviceName, String ipAddress,
                               Instant downSince, double currentResponseTimeMs, int consecutiveSuccesses) {
        super(deviceId, deviceName, ipAddress);
        this.downSince = downSince;
        // Only calculate downtime duration if downSince is not null (for actual recoveries)
        // For baseline healthy devices, downSince will be null
        this.downtimeDuration = downSince != null ? Duration.between(downSince, Instant.now()) : Duration.ZERO;
        this.currentResponseTimeMs = currentResponseTimeMs;
        this.consecutiveSuccesses = consecutiveSuccesses;
    }
}