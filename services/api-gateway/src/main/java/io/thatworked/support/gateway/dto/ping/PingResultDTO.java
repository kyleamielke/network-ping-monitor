package io.thatworked.support.gateway.dto.ping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Ping result DTO matching the exact response from ping-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PingResultDTO {
    private UUID deviceId;
    private Instant timestamp;
    private Long responseTimeMs;
    private boolean success;
    private String errorMessage;
}