package io.thatworked.support.alert.domain.port;

/**
 * Domain port for publishing events.
 * This interface abstracts away the messaging infrastructure.
 */
public interface EventPublisher {
    
    /**
     * Publish an alert created event.
     */
    void publishAlertCreated(Object event);
    
    /**
     * Publish an alert resolved event.
     */
    void publishAlertResolved(Object event);
    
    /**
     * Publish an alert acknowledged event.
     */
    void publishAlertAcknowledged(Object event);
}