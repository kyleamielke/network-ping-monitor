package io.thatworked.support.notification.domain.port;

import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.model.NotificationResult;

/**
 * Domain port for sending notifications through various channels.
 * Pure interface with no framework dependencies.
 */
public interface NotificationSender {
    
    /**
     * Send a notification through the specified channel.
     * 
     * @param request The notification request to send
     * @return The result of the notification attempt
     */
    NotificationResult send(NotificationRequest request);
    
    /**
     * Check if the sender supports a specific channel.
     * 
     * @param channelName The name of the channel
     * @return true if the channel is supported
     */
    boolean supportsChannel(String channelName);
}