package io.thatworked.support.notification.application.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for notification operations.
 * Contains primitive types only for clean architecture boundaries.
 */
public class NotificationResponse {
    private final UUID notificationRequestId;
    private final boolean successful;
    private final String message;
    private final String errorDetails;
    private final Instant sentAt;
    private final String channelSpecificId;
    
    public NotificationResponse(UUID notificationRequestId, boolean successful, String message,
                              String errorDetails, Instant sentAt, String channelSpecificId) {
        this.notificationRequestId = notificationRequestId;
        this.successful = successful;
        this.message = message;
        this.errorDetails = errorDetails;
        this.sentAt = sentAt;
        this.channelSpecificId = channelSpecificId;
    }
    
    public UUID getNotificationRequestId() {
        return notificationRequestId;
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getErrorDetails() {
        return errorDetails;
    }
    
    public Instant getSentAt() {
        return sentAt;
    }
    
    public String getChannelSpecificId() {
        return channelSpecificId;
    }
}