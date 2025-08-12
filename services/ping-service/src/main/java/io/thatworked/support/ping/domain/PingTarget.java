package io.thatworked.support.ping.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "ping_target")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PingTarget {
    @Id
    @Column(name = "device_id", columnDefinition = "uuid")
    private UUID deviceId;

    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "hostname")
    private String hostname;

    @Column(name = "is_monitored", nullable = false)
    private boolean isMonitored;

    @Column(name = "ping_interval_seconds")
    private Integer pingIntervalSeconds;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    // Optimistic locking - Disabled as it's not needed for this service
    // The ping-service has minimal concurrent updates and doesn't require optimistic locking
    // @Version
    // @Column(name = "version")
    // private Long version;

}