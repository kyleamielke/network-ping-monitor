package io.thatworked.support.report.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * Infrastructure DTO for ping statistics from ping service.
 */
public class PingStatisticsDTO {
    
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
    
    // Constructors
    public PingStatisticsDTO() {}
    
    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public String getTargetIp() { return targetIp; }
    public void setTargetIp(String targetIp) { this.targetIp = targetIp; }
    
    public String getTargetHostname() { return targetHostname; }
    public void setTargetHostname(String targetHostname) { this.targetHostname = targetHostname; }
    
    public Instant getPeriodStart() { return periodStart; }
    public void setPeriodStart(Instant periodStart) { this.periodStart = periodStart; }
    
    public Instant getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(Instant periodEnd) { this.periodEnd = periodEnd; }
    
    public long getTotalPings() { return totalPings; }
    public void setTotalPings(long totalPings) { this.totalPings = totalPings; }
    
    public long getSuccessfulPings() { return successfulPings; }
    public void setSuccessfulPings(long successfulPings) { this.successfulPings = successfulPings; }
    
    public long getFailedPings() { return failedPings; }
    public void setFailedPings(long failedPings) { this.failedPings = failedPings; }
    
    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
    
    public Double getAverageResponseTime() { return averageResponseTime; }
    public void setAverageResponseTime(Double averageResponseTime) { this.averageResponseTime = averageResponseTime; }
    
    public Double getMinResponseTime() { return minResponseTime; }
    public void setMinResponseTime(Double minResponseTime) { this.minResponseTime = minResponseTime; }
    
    public Double getMaxResponseTime() { return maxResponseTime; }
    public void setMaxResponseTime(Double maxResponseTime) { this.maxResponseTime = maxResponseTime; }
    
    public long getUptimeSeconds() { return uptimeSeconds; }
    public void setUptimeSeconds(long uptimeSeconds) { this.uptimeSeconds = uptimeSeconds; }
    
    public long getDowntimeSeconds() { return downtimeSeconds; }
    public void setDowntimeSeconds(long downtimeSeconds) { this.downtimeSeconds = downtimeSeconds; }
    
    public double getUptimePercentage() { return uptimePercentage; }
    public void setUptimePercentage(double uptimePercentage) { this.uptimePercentage = uptimePercentage; }
}