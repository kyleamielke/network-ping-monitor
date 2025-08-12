package io.thatworked.support.notification.domain.exception;

/**
 * Exception thrown when a notification fails to send.
 */
public class NotificationSendException extends NotificationDomainException {
    
    private final String channel;
    private final String recipient;
    
    public NotificationSendException(String message, String channel, String recipient) {
        super("NOTIFICATION_SEND_FAILED", message);
        this.channel = channel;
        this.recipient = recipient;
    }
    
    public NotificationSendException(String message, String channel, String recipient, Throwable cause) {
        super("NOTIFICATION_SEND_FAILED", message, cause);
        this.channel = channel;
        this.recipient = recipient;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public String getRecipient() {
        return recipient;
    }
}