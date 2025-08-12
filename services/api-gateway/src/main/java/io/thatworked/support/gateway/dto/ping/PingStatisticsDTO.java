package io.thatworked.support.gateway.dto.ping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Ping statistics DTO matching the response from ping-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PingStatisticsDTO {
    private UUID deviceId;
    private double successRate;
    private double averageRtt;
    private long recentFailures;
    private int totalSamples;
    private String lastStatus;
    private Double lastRtt;
    private Instant lastTime;
    
    // Additional fields
    private double uptime;
    private double packetLoss;
    private long successfulPings;
    private long failedPings;
}