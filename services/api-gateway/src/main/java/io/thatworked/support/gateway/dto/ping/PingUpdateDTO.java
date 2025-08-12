package io.thatworked.support.gateway.dto.ping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class PingUpdateDTO {
    private UUID deviceId;
    private Instant timestamp;
    private boolean success;
    private Long responseTimeMs;
    private String previousStatus;
    private String currentStatus;
}