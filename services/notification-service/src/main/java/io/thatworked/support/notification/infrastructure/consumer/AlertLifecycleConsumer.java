package io.thatworked.support.notification.infrastructure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.application.dto.SendNotificationCommand;
import io.thatworked.support.notification.application.usecase.SendNotificationUseCase;
import io.thatworked.support.notification.config.EventConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Kafka consumer for alert lifecycle events from the alert service.
 * Processes ALERT_CREATED and ALERT_RESOLVED events to send notifications.
 */
@Component
public class AlertLifecycleConsumer {
    
    private final StructuredLogger logger;
    private final SendNotificationUseCase sendNotificationUseCase;
    private final ObjectMapper objectMapper;
    private final DateTimeFormatter dateTimeFormatter;
    
    @Value("${notification-service.email.alert-recipient}")
    private String alertRecipient;
    
    public AlertLifecycleConsumer(StructuredLoggerFactory loggerFactory,
                                 SendNotificationUseCase sendNotificationUseCase,
                                 ObjectMapper objectMapper,
                                 DateTimeFormatter dateTimeFormatter) {
        this.logger = loggerFactory.getLogger(AlertLifecycleConsumer.class);
        this.sendNotificationUseCase = sendNotificationUseCase;
        this.objectMapper = objectMapper;
        this.dateTimeFormatter = dateTimeFormatter;
    }
    
    @KafkaListener(topics = "${notification-service.kafka.topics.alert-lifecycle-events:alert-lifecycle-events}", 
                   groupId = "${spring.kafka.consumer.group-id:notification-service}")
    public void handleAlertLifecycleEvent(
            @Payload String eventJson,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        logger.with("operation", "handleAlertLifecycleEvent")
                .with("topic", topic)
                .with("partition", partition)
                .with("offset", offset)
                .info("Kafka event received");
        
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> eventMap = objectMapper.readValue(eventJson, Map.class);
            
            String eventType = (String) eventMap.get("eventType");
            
            // Only process ALERT_CREATED and ALERT_RESOLVED events for notifications
            if ("ALERT_CREATED".equals(eventType)) {
                handleAlertCreated(eventMap);
            } else if ("ALERT_RESOLVED".equals(eventType)) {
                handleAlertResolved(eventMap);
            }
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.with("operation", "handleAlertLifecycleEvent")
                    .with("error", e.getMessage())
                    .error("Failed to process alert lifecycle event", e);
            acknowledgment.acknowledge(); // Acknowledge to prevent reprocessing
        }
    }
    
    private void handleAlertCreated(Map<String, Object> eventMap) {
        UUID alertId = UUID.fromString((String) eventMap.get("id"));
        UUID deviceId = UUID.fromString((String) eventMap.get("deviceId"));
        String deviceName = (String) eventMap.get("deviceName");
        String alertType = (String) eventMap.get("alertType");
        String message = (String) eventMap.get("message");
        
        // Create notification metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("alertId", alertId);
        metadata.put("deviceId", deviceId);
        metadata.put("deviceName", deviceName);
        metadata.put("alertType", alertType);
        metadata.put("ipAddress", eventMap.get("ipAddress"));
        metadata.put("consecutiveFailures", eventMap.get("consecutiveFailures"));
        metadata.put("failureReason", eventMap.get("failureReason"));
        
        // Format timestamp
        Instant timestamp = parseTimestamp(eventMap.get("timestamp"));
        metadata.put("alertTime", timestamp.atZone(java.time.ZoneId.systemDefault()).format(dateTimeFormatter));
        
        // Determine notification type based on alert type
        String notificationType = "DEVICE_DOWN"; // Use existing type for device down alerts
        String subject = String.format("Alert: %s - %s", deviceName, alertType);
        String body = String.format("Alert created for device %s: %s\n\nDetails: %s", 
            deviceName, alertType, message != null ? message : "No additional details");
        
        // Create notification command
        SendNotificationCommand command = new SendNotificationCommand(
            notificationType,
            "EMAIL", // Default to email for alerts
            alertRecipient,
            subject,
            body,
            metadata,
            deviceId
        );
        
        // Send notification
        sendNotificationUseCase.execute(command);
        
        logger.with("operation", "handleAlertCreated")
                .with("alertId", alertId)
                .with("deviceId", deviceId)
                .with("alertType", alertType)
                .info("Notification sent for alert created");
    }
    
    private void handleAlertResolved(Map<String, Object> eventMap) {
        UUID alertId = UUID.fromString((String) eventMap.get("id"));
        UUID deviceId = UUID.fromString((String) eventMap.get("deviceId"));
        String deviceName = (String) eventMap.get("deviceName");
        String alertType = (String) eventMap.get("alertType");
        
        // Create notification metadata
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("alertId", alertId);
        metadata.put("deviceId", deviceId);
        metadata.put("deviceName", deviceName);
        metadata.put("alertType", alertType);
        
        // Format timestamp
        Instant timestamp = parseTimestamp(eventMap.get("timestamp"));
        metadata.put("resolvedTime", timestamp.atZone(java.time.ZoneId.systemDefault()).format(dateTimeFormatter));
        
        String subject = String.format("Resolved: %s - %s", deviceName, alertType);
        String body = String.format("Alert resolved for device %s: %s", deviceName, alertType);
        
        // Create notification command
        SendNotificationCommand command = new SendNotificationCommand(
            "DEVICE_RECOVERED",
            "EMAIL",
            alertRecipient,
            subject,
            body,
            metadata,
            deviceId
        );
        
        // Send notification
        sendNotificationUseCase.execute(command);
        
        logger.with("operation", "handleAlertResolved")
                .with("alertId", alertId)
                .with("deviceId", deviceId)
                .with("alertType", alertType)
                .info("Notification sent for alert resolved");
    }
    
    private Instant parseTimestamp(Object timestampObj) {
        if (timestampObj instanceof Number) {
            double timestamp = ((Number) timestampObj).doubleValue();
            return Instant.ofEpochSecond((long) timestamp, (long) ((timestamp % 1) * 1_000_000_000));
        }
        return null;
    }
}