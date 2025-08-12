package io.thatworked.support.alert.infrastructure.event.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.alert.config.KafkaTopicsConfig;
import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.infrastructure.event.dto.AlertLifecycleEvent;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes alert events to Kafka.
 */
@Component
public class AlertEventPublisher {
    
    private final StructuredLogger logger;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaTopicsConfig kafkaTopicsConfig;
    
    public AlertEventPublisher(StructuredLoggerFactory loggerFactory, KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper,
                             KafkaTopicsConfig kafkaTopicsConfig) {
        this.logger = loggerFactory.getLogger(AlertEventPublisher.class);
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.kafkaTopicsConfig = kafkaTopicsConfig;
    }
    
    public void publishAlertCreated(Object alert) {
        if (alert instanceof AlertDomain) {
            AlertDomain alertDomain = (AlertDomain) alert;
            AlertLifecycleEvent event = AlertLifecycleEvent.builder()
                .id(alertDomain.getId())
                .deviceId(alertDomain.getDeviceId())
                .deviceName(alertDomain.getDeviceName())
                .alertType(alertDomain.getAlertType().name())
                .message(alertDomain.getMessage())
                .eventType(AlertLifecycleEvent.EventType.ALERT_CREATED)
                .eventTimestamp(java.time.Instant.now())
                .timestamp(alertDomain.getTimestamp())
                .ipAddress(alertDomain.getIpAddress())
                .consecutiveFailures(alertDomain.getConsecutiveFailures())
                .failureReason(alertDomain.getFailureReason())
                .build();
            
            publishEvent(event);
        }
    }
    
    public void publishAlertResolved(Object alert) {
        if (alert instanceof AlertDomain) {
            AlertDomain alertDomain = (AlertDomain) alert;
            AlertLifecycleEvent event = AlertLifecycleEvent.builder()
                .id(alertDomain.getId())
                .deviceId(alertDomain.getDeviceId())
                .deviceName(alertDomain.getDeviceName())
                .alertType(alertDomain.getAlertType().name())
                .eventType(AlertLifecycleEvent.EventType.ALERT_RESOLVED)
                .eventTimestamp(java.time.Instant.now())
                .timestamp(alertDomain.getResolvedAt())
                .build();
            
            publishEvent(event);
        }
    }
    
    public void publishAlertAcknowledged(Object alert) {
        if (alert instanceof AlertDomain) {
            AlertDomain alertDomain = (AlertDomain) alert;
            AlertLifecycleEvent event = AlertLifecycleEvent.builder()
                .id(alertDomain.getId())
                .deviceId(alertDomain.getDeviceId())
                .deviceName(alertDomain.getDeviceName())
                .alertType(alertDomain.getAlertType().name())
                .eventType(AlertLifecycleEvent.EventType.ALERT_ACKNOWLEDGED)
                .eventTimestamp(java.time.Instant.now())
                .acknowledgedBy(alertDomain.getAcknowledgedBy())
                .timestamp(alertDomain.getAcknowledgedAt())
                .build();
            
            publishEvent(event);
        }
    }
    
    private void publishEvent(AlertLifecycleEvent event) {
        try {
            kafkaTemplate.send(kafkaTopicsConfig.getAlertLifecycleEvents(), event.getId().toString(), event);
            logger.with("eventType", event.getEventType())
                  .with("alertId", event.getId())
                  .with("deviceId", event.getDeviceId())
                  .info("Published alert lifecycle event");
        } catch (Exception e) {
            logger.with("eventType", event.getEventType())
                  .with("alertId", event.getId())
                  .error("Failed to publish alert lifecycle event", e);
        }
    }
}