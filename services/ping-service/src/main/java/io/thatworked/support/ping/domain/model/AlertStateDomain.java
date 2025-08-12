package io.thatworked.support.ping.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model for alert state tracking.
 * Pure domain object without infrastructure dependencies.
 */
public class AlertStateDomain {
    private final UUID deviceId;
    private final boolean alertActive;
    private final int consecutiveFailures;
    private final int consecutiveSuccesses;
    private final Instant lastAlertSent;
    private final Instant lastRecoverySent;
    private final Instant lastFailureTime;
    private final Instant lastSuccessTime;
    private final Instant createdAt;
    private final Instant updatedAt;

    private AlertStateDomain(Builder builder) {
        this.deviceId = builder.deviceId;
        this.alertActive = builder.alertActive;
        this.consecutiveFailures = builder.consecutiveFailures;
        this.consecutiveSuccesses = builder.consecutiveSuccesses;
        this.lastAlertSent = builder.lastAlertSent;
        this.lastRecoverySent = builder.lastRecoverySent;
        this.lastFailureTime = builder.lastFailureTime;
        this.lastSuccessTime = builder.lastSuccessTime;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // Getters
    public UUID getDeviceId() { return deviceId; }
    public boolean isAlertActive() { return alertActive; }
    public int getConsecutiveFailures() { return consecutiveFailures; }
    public int getConsecutiveSuccesses() { return consecutiveSuccesses; }
    public Instant getLastAlertSent() { return lastAlertSent; }
    public Instant getLastRecoverySent() { return lastRecoverySent; }
    public Instant getLastFailureTime() { return lastFailureTime; }
    public Instant getLastSuccessTime() { return lastSuccessTime; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    // Domain methods
    public AlertStateDomain recordSuccess() {
        return builder()
            .deviceId(deviceId)
            .alertActive(alertActive)
            .consecutiveFailures(0)
            .consecutiveSuccesses(consecutiveSuccesses + 1)
            .lastAlertSent(lastAlertSent)
            .lastRecoverySent(lastRecoverySent)
            .lastFailureTime(lastFailureTime)
            .lastSuccessTime(Instant.now())
            .createdAt(createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    public AlertStateDomain recordFailure() {
        return builder()
            .deviceId(deviceId)
            .alertActive(alertActive)
            .consecutiveFailures(consecutiveFailures + 1)
            .consecutiveSuccesses(0)
            .lastAlertSent(lastAlertSent)
            .lastRecoverySent(lastRecoverySent)
            .lastFailureTime(Instant.now())
            .lastSuccessTime(lastSuccessTime)
            .createdAt(createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    public AlertStateDomain activateAlert() {
        return builder()
            .deviceId(deviceId)
            .alertActive(true)
            .consecutiveFailures(consecutiveFailures)
            .consecutiveSuccesses(consecutiveSuccesses)
            .lastAlertSent(Instant.now())
            .lastRecoverySent(lastRecoverySent)
            .lastFailureTime(lastFailureTime)
            .lastSuccessTime(lastSuccessTime)
            .createdAt(createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    public AlertStateDomain deactivateAlert() {
        return builder()
            .deviceId(deviceId)
            .alertActive(false)
            .consecutiveFailures(consecutiveFailures)
            .consecutiveSuccesses(consecutiveSuccesses)
            .lastAlertSent(lastAlertSent)
            .lastRecoverySent(Instant.now())
            .lastFailureTime(lastFailureTime)
            .lastSuccessTime(lastSuccessTime)
            .createdAt(createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    public boolean shouldTriggerAlert(int failureThreshold) {
        return !alertActive && consecutiveFailures >= failureThreshold;
    }

    public boolean shouldResolveAlert(int recoveryThreshold) {
        return alertActive && consecutiveSuccesses >= recoveryThreshold;
    }

    // Static factory methods
    public static AlertStateDomain createNew(UUID deviceId) {
        Instant now = Instant.now();
        return builder()
            .deviceId(deviceId)
            .alertActive(false)
            .consecutiveFailures(0)
            .consecutiveSuccesses(0)
            .lastAlertSent(null)
            .lastRecoverySent(null)
            .lastFailureTime(null)
            .lastSuccessTime(null)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID deviceId;
        private boolean alertActive;
        private int consecutiveFailures;
        private int consecutiveSuccesses;
        private Instant lastAlertSent;
        private Instant lastRecoverySent;
        private Instant lastFailureTime;
        private Instant lastSuccessTime;
        private Instant createdAt;
        private Instant updatedAt;

        public Builder deviceId(UUID deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder alertActive(boolean alertActive) {
            this.alertActive = alertActive;
            return this;
        }

        public Builder consecutiveFailures(int consecutiveFailures) {
            this.consecutiveFailures = consecutiveFailures;
            return this;
        }

        public Builder consecutiveSuccesses(int consecutiveSuccesses) {
            this.consecutiveSuccesses = consecutiveSuccesses;
            return this;
        }

        public Builder lastAlertSent(Instant lastAlertSent) {
            this.lastAlertSent = lastAlertSent;
            return this;
        }

        public Builder lastRecoverySent(Instant lastRecoverySent) {
            this.lastRecoverySent = lastRecoverySent;
            return this;
        }

        public Builder lastFailureTime(Instant lastFailureTime) {
            this.lastFailureTime = lastFailureTime;
            return this;
        }

        public Builder lastSuccessTime(Instant lastSuccessTime) {
            this.lastSuccessTime = lastSuccessTime;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AlertStateDomain build() {
            return new AlertStateDomain(this);
        }
    }
}