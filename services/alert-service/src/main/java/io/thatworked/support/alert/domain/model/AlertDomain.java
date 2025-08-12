package io.thatworked.support.alert.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Pure domain model for Alert.
 * No framework dependencies, no annotations except standard Java.
 * This represents the core business entity for alerts.
 */
public class AlertDomain {
    
    private final UUID id;
    private final UUID deviceId;
    private final String deviceName;
    private final AlertType alertType;
    private final String message;
    private final Instant timestamp;
    private boolean isResolved;
    private Instant resolvedAt;
    private boolean isAcknowledged;
    private Instant acknowledgedAt;
    private String acknowledgedBy;
    private final Instant createdAt;
    private Instant updatedAt;
    private final String ipAddress;
    private final Integer consecutiveFailures;
    private final String failureReason;
    private Long version;

    // Constructor for new alerts
    public AlertDomain(UUID deviceId, String deviceName, AlertType alertType, String message) {
        this(deviceId, deviceName, alertType, message, null, null, null);
    }
    
    // Constructor for new alerts with metadata
    public AlertDomain(UUID deviceId, String deviceName, AlertType alertType, String message,
                      String ipAddress, Integer consecutiveFailures, String failureReason) {
        this.id = UUID.randomUUID();
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.alertType = alertType;
        this.message = message;
        this.timestamp = Instant.now();
        this.isResolved = false;
        this.isAcknowledged = false;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.ipAddress = ipAddress;
        this.consecutiveFailures = consecutiveFailures;
        this.failureReason = failureReason;
        this.version = null; // New alerts start with null version (Hibernate will set it)
    }

    // Constructor for existing alerts (from persistence)
    public AlertDomain(UUID id, UUID deviceId, String deviceName, AlertType alertType, 
                      String message, Instant timestamp, boolean isResolved, Instant resolvedAt,
                      boolean isAcknowledged, Instant acknowledgedAt, String acknowledgedBy,
                      Instant createdAt, Instant updatedAt, String ipAddress, 
                      Integer consecutiveFailures, String failureReason, Long version) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.alertType = alertType;
        this.message = message;
        this.timestamp = timestamp;
        this.isResolved = isResolved;
        this.resolvedAt = resolvedAt;
        this.isAcknowledged = isAcknowledged;
        this.acknowledgedAt = acknowledgedAt;
        this.acknowledgedBy = acknowledgedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ipAddress = ipAddress;
        this.consecutiveFailures = consecutiveFailures;
        this.failureReason = failureReason;
        this.version = version;
    }

    // Business methods
    public void acknowledge(String acknowledgedBy) {
        if (this.isAcknowledged) {
            throw new IllegalStateException("Alert is already acknowledged");
        }
        this.isAcknowledged = true;
        this.acknowledgedAt = Instant.now();
        this.acknowledgedBy = acknowledgedBy;
        this.updatedAt = Instant.now();
    }

    public void markResolved() {
        if (this.isResolved) {
            throw new IllegalStateException("Alert is already resolved");
        }
        this.isResolved = true;
        this.resolvedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean canBeAutoResolved() {
        return !this.isResolved && 
               (this.alertType == AlertType.DEVICE_DOWN || 
                this.alertType == AlertType.HIGH_RESPONSE_TIME ||
                this.alertType == AlertType.PACKET_LOSS);
    }

    public boolean isActive() {
        return !this.isResolved;
    }

    public boolean isUnacknowledged() {
        return !this.isAcknowledged;
    }

    // Getters only (immutable from outside except through business methods)
    public UUID getId() {
        return id;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public String getMessage() {
        return message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }

    public boolean isAcknowledged() {
        return isAcknowledged;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public Integer getConsecutiveFailures() {
        return consecutiveFailures;
    }
    public String getFailureReason() {
        return failureReason;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "AlertDomain{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", deviceName='" + deviceName + '\'' +
                ", alertType=" + alertType +
                ", resolved=" + isResolved +
                ", acknowledged=" + isAcknowledged +
                '}';
    }
}