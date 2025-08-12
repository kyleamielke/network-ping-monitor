package io.thatworked.support.ping.api.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PingStatisticsDTO {
    private UUID deviceId;
    private double successRate;
    private double averageRtt;
    private long recentFailures;
    private int totalSamples;
    private String lastStatus;
    private Double lastRtt;
    private Instant lastTime;
    
    // Additional fields needed by frontend
    private double uptime;           // Uptime percentage (successRate * 100)
    private double packetLoss;       // Packet loss percentage
    private long successfulPings;    // Number of successful pings
    private long failedPings;        // Number of failed pings (same as recentFailures)
}