package io.thatworked.support.report.infrastructure.dto;

import java.util.UUID;

/**
 * Infrastructure DTO for ping target from ping service.
 */
public class PingTargetDTO {
    private UUID deviceId;
    private String ipAddress;
    private boolean monitored;
    private Integer pingIntervalSeconds;
    
    // Constructors
    public PingTargetDTO() {}
    
    // Getters and Setters
    public UUID getDeviceId() { return deviceId; }
    public void setDeviceId(UUID deviceId) { this.deviceId = deviceId; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public boolean isMonitored() { return monitored; }
    public void setMonitored(boolean monitored) { this.monitored = monitored; }
    
    public Integer getPingIntervalSeconds() { return pingIntervalSeconds; }
    public void setPingIntervalSeconds(Integer pingIntervalSeconds) { this.pingIntervalSeconds = pingIntervalSeconds; }
}