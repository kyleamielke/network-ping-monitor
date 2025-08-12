package io.thatworked.support.notification.domain.exception;

/**
 * Exception thrown when notification repository operations fail.
 * This is a domain exception that wraps infrastructure failures.
 */
public class NotificationRepositoryException extends NotificationDomainException {
    
    public NotificationRepositoryException(String message) {
        super("NOTIFICATION_REPOSITORY_ERROR", message);
    }
    
    public NotificationRepositoryException(String message, Throwable cause) {
        super("NOTIFICATION_REPOSITORY_ERROR", message, cause);
    }
}