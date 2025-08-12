package io.thatworked.support.ping.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PingTargetDTO {
    private UUID deviceId;
    private String ipAddress;
    private String hostname;
    private boolean monitored;
    private Integer pingIntervalSeconds;
    private Instant createdAt;
    private Instant updatedAt;
}