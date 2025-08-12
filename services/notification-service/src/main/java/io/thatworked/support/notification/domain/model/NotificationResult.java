package io.thatworked.support.notification.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain value object representing the result of a notification attempt.
 * Immutable object with no framework dependencies.
 */
public class NotificationResult {
    private final UUID notificationRequestId;
    private final boolean successful;
    private final String message;
    private final String errorDetails;
    private final Instant sentAt;
    private final String channelSpecificId; // e.g., email message ID, Slack message ID
    
    // Success constructor
    public static NotificationResult success(UUID notificationRequestId, String message, 
                                           String channelSpecificId) {
        return new NotificationResult(notificationRequestId, true, message, null, 
                                    Instant.now(), channelSpecificId);
    }
    
    // Failure constructor
    public static NotificationResult failure(UUID notificationRequestId, String message, 
                                           String errorDetails) {
        return new NotificationResult(notificationRequestId, false, message, errorDetails, 
                                    Instant.now(), null);
    }
    
    private NotificationResult(UUID notificationRequestId, boolean successful, String message,
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