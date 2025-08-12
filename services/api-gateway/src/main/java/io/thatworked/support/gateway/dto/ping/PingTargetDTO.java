package io.thatworked.support.gateway.dto.ping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Ping target DTO matching the exact response from ping-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PingTargetDTO {
    private UUID deviceId;
    private String ipAddress;
    private String hostname;
    private boolean monitored;
    private int pingIntervalSeconds;
    private Instant createdAt;
    private Instant updatedAt;
}