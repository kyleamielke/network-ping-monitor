package io.thatworked.support.notification.infrastructure.consumer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DeviceDownEvent extends DeviceAlertEvent {
    private int consecutiveFailures;
    private Instant lastSuccessTime;
    private String failureReason;
}