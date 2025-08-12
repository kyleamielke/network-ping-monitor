package io.thatworked.support.notification.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for notification results.
 */
@Entity
@Table(name = "notification_results")
@Getter
@Setter
public class NotificationResultEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "notification_request_id", nullable = false)
    private UUID notificationRequestId;
    
    @Column(name = "successful", nullable = false)
    private boolean isSuccessful;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails;
    
    @Column(name = "sent_at", nullable = false)
    @CreationTimestamp
    private Instant sentAt;
    
    @Column(name = "channel_specific_id")
    private String channelSpecificId;
}