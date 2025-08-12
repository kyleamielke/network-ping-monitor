package io.thatworked.support.ping.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model for ping results.
 * Pure domain object without infrastructure dependencies.
 */
public class PingResultDomain {
    private final UUID deviceId;
    private final String ipAddress;
    private final boolean success;
    private final Double responseTime;
    private final String errorMessage;
    private final Instant timestamp;

    public PingResultDomain(UUID deviceId, String ipAddress, boolean success, 
                           Double responseTime, String errorMessage, Instant timestamp) {
        this.deviceId = deviceId;
        this.ipAddress = ipAddress;
        this.success = success;
        this.responseTime = responseTime;
        this.errorMessage = errorMessage;
        this.timestamp = timestamp;
    }

    // Getters
    public UUID getDeviceId() { return deviceId; }
    public String getIpAddress() { return ipAddress; }
    public boolean isSuccess() { return success; }
    public Double getResponseTime() { return responseTime; }
    public String getErrorMessage() { return errorMessage; }
    public Instant getTimestamp() { return timestamp; }

    // Static factory methods
    public static PingResultDomain create(UUID deviceId, String ipAddress, boolean success, 
                                         Long responseTime, java.time.Instant timestamp) {
        return new PingResultDomain(deviceId, ipAddress, success, 
                                   responseTime != null ? responseTime.doubleValue() : 0.0, 
                                   success ? null : "Request timeout", 
                                   timestamp);
    }
    
    public static PingResultDomain success(UUID deviceId, String ipAddress, Double responseTime) {
        return new PingResultDomain(deviceId, ipAddress, true, responseTime, null, Instant.now());
    }

    public static PingResultDomain failure(UUID deviceId, String ipAddress, String errorMessage) {
        return new PingResultDomain(deviceId, ipAddress, false, null, errorMessage, Instant.now());
    }

    // Domain methods
    public boolean isFailure() {
        return !success;
    }

    public boolean hasTimeout() {
        return errorMessage != null && errorMessage.toLowerCase().contains("timeout");
    }

    public boolean hasError() {
        return errorMessage != null && !errorMessage.trim().isEmpty();
    }
}