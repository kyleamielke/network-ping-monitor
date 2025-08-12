package io.thatworked.support.report.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

/**
 * Infrastructure DTO for ping result from ping service.
 */
public class PingResultDTO {
    
    private String deviceId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant timestamp;
    
    private boolean success;
    private Long responseTimeMs;
    private String errorMessage;
    
    // Constructors
    public PingResultDTO() {}
    
    public PingResultDTO(String deviceId, Instant timestamp, 
                        boolean success, Long responseTimeMs, String errorMessage) {
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.success = success;
        this.responseTimeMs = responseTimeMs;
        this.errorMessage = errorMessage;
    }
    
    // Getters and Setters
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    
    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}