package io.thatworked.support.notification.domain.port;

import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.model.NotificationResult;

/**
 * Domain port for publishing notification events.
 * Pure interface with no framework dependencies.
 */
public interface EventPublisher {
    
    /**
     * Publish that a notification was requested.
     * 
     * @param request The notification request
     */
    void publishNotificationRequested(NotificationRequest request);
    
    /**
     * Publish that a notification was sent successfully.
     * 
     * @param request The original notification request
     * @param result The successful result
     */
    void publishNotificationSent(NotificationRequest request, NotificationResult result);
    
    /**
     * Publish that a notification failed to send.
     * 
     * @param request The original notification request
     * @param result The failure result
     */
    void publishNotificationFailed(NotificationRequest request, NotificationResult result);
}