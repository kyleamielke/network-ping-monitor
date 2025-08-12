package io.thatworked.support.notification.domain.exception;

/**
 * Exception thrown when attempting to use an unsupported notification channel.
 */
public class UnsupportedChannelException extends NotificationDomainException {
    
    private final String channel;
    
    public UnsupportedChannelException(String channel) {
        super("UNSUPPORTED_CHANNEL", "Unsupported notification channel: " + channel);
        this.channel = channel;
    }
    
    public String getChannel() {
        return channel;
    }
}