package io.thatworked.support.alert.infrastructure.entity;

import io.thatworked.support.alert.domain.model.AlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alerts_device_id", columnList = "device_id"),
    @Index(name = "idx_alerts_timestamp", columnList = "timestamp"),
    @Index(name = "idx_alerts_type", columnList = "alert_type"),
    @Index(name = "idx_alerts_resolved", columnList = "resolved"),
    @Index(name = "idx_alerts_acknowledged", columnList = "acknowledged"),
    @Index(name = "idx_alerts_created_at", columnList = "created_at"),
    @Index(name = "idx_alerts_device_unresolved", columnList = "device_id, resolved"),
    @Index(name = "idx_alerts_device_unacknowledged", columnList = "device_id, acknowledged")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;

    @Column(name = "resolved", nullable = false)
    @Builder.Default
    private boolean isResolved = false;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "acknowledged", nullable = false)
    @Builder.Default
    private boolean isAcknowledged = false;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Column(name = "acknowledged_by")
    private String acknowledgedBy;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private Instant updatedAt = Instant.now();
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "consecutive_failures")
    private Integer consecutiveFailures;
    
    @Column(name = "failure_reason")
    private String failureReason;

    // Optimistic locking
    @Version
    @Column(name = "version")
    private Long version;

    // Lifecycle methods
    public void acknowledge(String acknowledgedBy) {
        this.isAcknowledged = true;
        this.acknowledgedAt = Instant.now();
        this.acknowledgedBy = acknowledgedBy;
        this.updatedAt = Instant.now();
    }

    public void markResolved() {
        this.isResolved = true;
        this.resolvedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}