package io.thatworked.support.notification.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Domain value object representing a notification request.
 * Immutable object with no framework dependencies.
 */
public class NotificationRequest {
    private final UUID id;
    private final NotificationType type;
    private final NotificationChannel channel;
    private final String recipient;
    private final String subject;
    private final String message;
    private final Map<String, Object> metadata;
    private final Instant requestedAt;
    private final UUID sourceEventId;
    
    // Constructor for creating new requests
    public NotificationRequest(NotificationType type, NotificationChannel channel, 
                             String recipient, String subject, String message,
                             Map<String, Object> metadata, UUID sourceEventId) {
        this.id = UUID.randomUUID();
        this.type = type;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
        this.requestedAt = Instant.now();
        this.sourceEventId = sourceEventId;
    }
    
    // Constructor for reconstituting from persistence
    public NotificationRequest(UUID id, NotificationType type, NotificationChannel channel,
                             String recipient, String subject, String message,
                             Map<String, Object> metadata, Instant requestedAt, UUID sourceEventId) {
        this.id = id;
        this.type = type;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
        this.requestedAt = requestedAt;
        this.sourceEventId = sourceEventId;
    }
    
    public UUID getId() {
        return id;
    }
    
    public NotificationType getType() {
        return type;
    }
    
    public NotificationChannel getChannel() {
        return channel;
    }
    
    public String getRecipient() {
        return recipient;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getMessage() {
        return message;
    }
    
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }
    
    public Instant getRequestedAt() {
        return requestedAt;
    }
    
    public UUID getSourceEventId() {
        return sourceEventId;
    }
    
    public Object getMetadataValue(String key) {
        return metadata.get(key);
    }
}