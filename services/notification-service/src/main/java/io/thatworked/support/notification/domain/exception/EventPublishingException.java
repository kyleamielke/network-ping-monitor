package io.thatworked.support.notification.domain.exception;

/**
 * Exception thrown when event publishing operations fail.
 * This is a domain exception that wraps infrastructure failures.
 */
public class EventPublishingException extends NotificationDomainException {
    
    public EventPublishingException(String message) {
        super("EVENT_PUBLISHING_ERROR", message);
    }
    
    public EventPublishingException(String message, Throwable cause) {
        super("EVENT_PUBLISHING_ERROR", message, cause);
    }
}