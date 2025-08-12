package io.thatworked.support.ping.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alert_states")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlertState {

    @Id
    @Column(name = "device_id", columnDefinition = "uuid")
    private UUID deviceId;

    @Column(name = "consecutive_failures", nullable = false)
    private int consecutiveFailures = 0;

    @Column(name = "consecutive_successes", nullable = false)
    private int consecutiveSuccesses = 0;

    @Column(name = "is_alerting", nullable = false)
    private boolean isAlerting = false;

    @Column(name = "last_alert_sent")
    private Instant lastAlertSent;

    @Column(name = "last_recovery_sent")
    private Instant lastRecoverySent;

    @Column(name = "last_failure_time")
    private Instant lastFailureTime;

    @Column(name = "last_success_time")
    private Instant lastSuccessTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    public void recordSuccess() {
        consecutiveSuccesses++;
        consecutiveFailures = 0;
        lastSuccessTime = Instant.now();
        // updatedAt is handled by @UpdateTimestamp
    }

    public void recordFailure() {
        consecutiveFailures++;
        consecutiveSuccesses = 0;
        lastFailureTime = Instant.now();
        // updatedAt is handled by @UpdateTimestamp
    }

    public void markAlerting() {
        isAlerting = true;
        lastAlertSent = Instant.now();
        // updatedAt is handled by @UpdateTimestamp
    }

    public void markRecovered() {
        isAlerting = false;
        lastRecoverySent = Instant.now();
        // updatedAt is handled by @UpdateTimestamp
    }
}