package io.thatworked.support.notification.infrastructure.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.domain.exception.EventPublishingException;
import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.model.NotificationResult;
import io.thatworked.support.notification.domain.port.EventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka implementation of the EventPublisher port.
 */
@Component
public class KafkaEventPublisher implements EventPublisher {
    
    private final StructuredLogger logger;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${notification-service.kafka.topics.notification-events:notification-events}")
    private String notificationEventsTopic;
    
    @Value("${notification-service.events.types.requested:NOTIFICATION_REQUESTED}")
    private String eventTypeRequested;
    
    @Value("${notification-service.events.types.sent:NOTIFICATION_SENT}")
    private String eventTypeSent;
    
    @Value("${notification-service.events.types.failed:NOTIFICATION_FAILED}")
    private String eventTypeFailed;
    
    public KafkaEventPublisher(StructuredLoggerFactory loggerFactory,
                              KafkaTemplate<String, Object> kafkaTemplate, 
                              ObjectMapper objectMapper) {
        this.logger = loggerFactory.getLogger(KafkaEventPublisher.class);
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public void publishNotificationRequested(NotificationRequest request) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventTypeRequested);
            event.put("notificationId", request.getId());
            event.put("notificationType", request.getType().name());
            event.put("channel", request.getChannel().name());
            event.put("recipient", request.getRecipient());
            event.put("sourceEventId", request.getSourceEventId());
            event.put("timestamp", request.getRequestedAt());
            
            kafkaTemplate.send(notificationEventsTopic, event);
            
            logger.with("operation", "publishNotificationRequested")
                    .with("notificationId", request.getId())
                    .with("notificationType", request.getType().name())
                    .debug("Published notification requested event");
        } catch (Exception e) {
            logger.with("operation", "publishNotificationRequested")
                    .with("notificationId", request.getId())
                    .with("error", e.getMessage())
                    .error("Failed to publish notification requested event", e);
            throw new EventPublishingException("Failed to publish notification requested event", e);
        }
    }
    
    @Override
    public void publishNotificationSent(NotificationRequest request, NotificationResult result) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventTypeSent);
            event.put("notificationId", request.getId());
            event.put("notificationType", request.getType().name());
            event.put("channel", request.getChannel().name());
            event.put("recipient", request.getRecipient());
            event.put("channelSpecificId", result.getChannelSpecificId());
            event.put("timestamp", result.getSentAt());
            
            kafkaTemplate.send(notificationEventsTopic, event);
            
            logger.with("operation", "publishNotificationSent")
                    .with("notificationId", request.getId())
                    .with("channelSpecificId", result.getChannelSpecificId())
                    .debug("Published notification sent event");
        } catch (Exception e) {
            logger.with("operation", "publishNotificationSent")
                    .with("notificationId", request.getId())
                    .with("error", e.getMessage())
                    .error("Failed to publish notification sent event", e);
            throw new EventPublishingException("Failed to publish notification sent event", e);
        }
    }
    
    @Override
    public void publishNotificationFailed(NotificationRequest request, NotificationResult result) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", eventTypeFailed);
            event.put("notificationId", request.getId());
            event.put("notificationType", request.getType().name());
            event.put("channel", request.getChannel().name());
            event.put("recipient", request.getRecipient());
            event.put("errorMessage", result.getMessage());
            event.put("errorDetails", result.getErrorDetails());
            event.put("timestamp", result.getSentAt());
            
            kafkaTemplate.send(notificationEventsTopic, event);
            
            logger.with("operation", "publishNotificationFailed")
                    .with("notificationId", request.getId())
                    .with("errorMessage", result.getMessage())
                    .debug("Published notification failed event");
        } catch (Exception e) {
            logger.with("operation", "publishNotificationFailed")
                    .with("notificationId", request.getId())
                    .with("error", e.getMessage())
                    .error("Failed to publish notification failed event", e);
            throw new EventPublishingException("Failed to publish notification failed event", e);
        }
    }
}