package io.thatworked.support.report.infrastructure.dto;

import java.time.Instant;

/**
 * Infrastructure DTO for alert search criteria.
 */
public class AlertSearchCriteria {
    
    private String deviceId;
    private String alertType;
    private String status;
    private Instant startDate;
    private Instant endDate;
    
    // Constructors
    public AlertSearchCriteria() {}
    
    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Instant getStartDate() { return startDate; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    
    public Instant getEndDate() { return endDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
}