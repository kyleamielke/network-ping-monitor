package io.thatworked.support.gateway.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceStatusEvent {
    private UUID deviceId;
    private boolean online;
    private Instant lastSeenAt;
    private Long responseTimeMs;
    private Integer consecutiveFailures;
}