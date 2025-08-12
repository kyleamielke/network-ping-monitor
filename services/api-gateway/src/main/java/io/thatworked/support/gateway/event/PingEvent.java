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
public class PingEvent {
    private UUID deviceId;
    private Instant timestamp;
    private boolean success;
    private Long responseTimeMs;
    private String errorMessage;
}