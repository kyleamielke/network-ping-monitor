package io.thatworked.support.alert.infrastructure.adapter;

import io.thatworked.support.alert.domain.port.EventPublisher;
import io.thatworked.support.alert.infrastructure.event.publisher.AlertEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter implementing the EventPublisher port.
 * Delegates to the existing AlertEventPublisher for Kafka integration.
 */
@Component
public class EventPublisherAdapter implements EventPublisher {
    
    private final AlertEventPublisher kafkaPublisher;
    
    public EventPublisherAdapter(AlertEventPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }
    
    @Override
    public void publishAlertCreated(Object event) {
        kafkaPublisher.publishAlertCreated(event);
    }
    
    @Override
    public void publishAlertResolved(Object event) {
        kafkaPublisher.publishAlertResolved(event);
    }
    
    @Override
    public void publishAlertAcknowledged(Object event) {
        kafkaPublisher.publishAlertAcknowledged(event);
    }
}