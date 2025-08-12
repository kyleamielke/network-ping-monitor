package io.thatworked.support.notification.application.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for notification history queries.
 * Contains primitive types only for clean architecture boundaries.
 */
public class NotificationHistoryResponse {
    private final UUID id;
    private final String notificationType;
    private final String channel;
    private final String recipient;
    private final String subject;
    private final String message;
    private final Map<String, Object> metadata;
    private final Instant requestedAt;
    private final UUID sourceEventId;
    
    public NotificationHistoryResponse(UUID id, String notificationType, String channel,
                                     String recipient, String subject, String message,
                                     Map<String, Object> metadata, Instant requestedAt,
                                     UUID sourceEventId) {
        this.id = id;
        this.notificationType = notificationType;
        this.channel = channel;
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
        this.metadata = metadata;
        this.requestedAt = requestedAt;
        this.sourceEventId = sourceEventId;
    }
    
    public UUID getId() {
        return id;
    }
    
    public String getNotificationType() {
        return notificationType;
    }
    
    public String getChannel() {
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
        return metadata;
    }
    
    public Instant getRequestedAt() {
        return requestedAt;
    }
    
    public UUID getSourceEventId() {
        return sourceEventId;
    }
}