package io.thatworked.support.notification.domain.exception;

/**
 * Exception thrown when a notification request is invalid.
 */
public class InvalidNotificationRequestException extends NotificationDomainException {
    
    public InvalidNotificationRequestException(String message) {
        super("INVALID_NOTIFICATION_REQUEST", message);
    }
}