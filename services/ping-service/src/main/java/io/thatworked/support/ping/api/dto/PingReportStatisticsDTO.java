package io.thatworked.support.ping.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.Instant;

@Data
public class PingReportStatisticsDTO {
    
    private String deviceId;
    private String deviceName;
    private String targetIp;
    private String targetHostname;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant periodStart;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant periodEnd;
    
    private long totalPings;
    private long successfulPings;
    private long failedPings;
    private double successRate;
    private Double averageResponseTime;
    private Double minResponseTime;
    private Double maxResponseTime;
    private long uptimeSeconds;
    private long downtimeSeconds;
    private double uptimePercentage;
}