package io.thatworked.support.notification.infrastructure.consumer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeviceRecoveredEvent extends DeviceAlertEvent {
    private Instant downSince;
    private Duration downtimeDuration;
    private double currentResponseTimeMs;
    private int consecutiveSuccesses;
}