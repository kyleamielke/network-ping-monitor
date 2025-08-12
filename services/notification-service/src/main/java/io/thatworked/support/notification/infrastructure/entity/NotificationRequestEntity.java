package io.thatworked.support.notification.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity for notification requests.
 */
@Entity
@Table(name = "notification_requests")
@Getter
@Setter
public class NotificationRequestEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "notification_type", nullable = false)
    private String notificationType;
    
    @Column(nullable = false)
    private String channel;
    
    @Column(nullable = false)
    private String recipient;
    
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @ElementCollection
    @CollectionTable(name = "notification_metadata", 
                    joinColumns = @JoinColumn(name = "notification_request_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata = new HashMap<>();
    
    @Column(name = "requested_at", nullable = false)
    @CreationTimestamp
    private Instant requestedAt;
    
    @Column(name = "source_event_id")
    private UUID sourceEventId;
}