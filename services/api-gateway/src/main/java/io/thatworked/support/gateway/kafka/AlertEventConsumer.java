package io.thatworked.support.gateway.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.event.AlertEvent;
import io.thatworked.support.gateway.resolver.SubscriptionResolver;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class AlertEventConsumer {
    
    private final StructuredLogger logger;
    private final SubscriptionResolver subscriptionResolver;
    private final ObjectMapper objectMapper;
    
    public AlertEventConsumer(StructuredLoggerFactory loggerFactory,
                             SubscriptionResolver subscriptionResolver,
                             ObjectMapper objectMapper) {
        this.logger = loggerFactory.getLogger(AlertEventConsumer.class);
        this.subscriptionResolver = subscriptionResolver;
        this.objectMapper = objectMapper;
    }
    
    @KafkaListener(topics = "alert-lifecycle-events", groupId = "api-gateway")
    public void consumeAlertEvent(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            
            String eventType = jsonNode.get("eventType").asText();
            JsonNode alertNode = jsonNode.get("alert");
            
            AlertDTO alert = AlertDTO.builder()
                .id(UUID.fromString(alertNode.get("id").asText()))
                .deviceId(UUID.fromString(alertNode.get("deviceId").asText()))
                .deviceName(alertNode.get("deviceName").asText())
                .alertType(alertNode.get("alertType").asText())
                .message(alertNode.get("message").asText())
                .timestamp(Instant.parse(alertNode.get("timestamp").asText()))
                .resolved(alertNode.get("resolved").asBoolean())
                .resolvedAt(alertNode.has("resolvedAt") && !alertNode.get("resolvedAt").isNull()
                    ? Instant.parse(alertNode.get("resolvedAt").asText()) : null)
                .acknowledged(alertNode.get("acknowledged").asBoolean())
                .acknowledgedAt(alertNode.has("acknowledgedAt") && !alertNode.get("acknowledgedAt").isNull()
                    ? Instant.parse(alertNode.get("acknowledgedAt").asText()) : null)
                .acknowledgedBy(alertNode.has("acknowledgedBy") && !alertNode.get("acknowledgedBy").isNull()
                    ? alertNode.get("acknowledgedBy").asText() : null)
                .createdAt(Instant.parse(alertNode.get("createdAt").asText()))
                .updatedAt(Instant.parse(alertNode.get("updatedAt").asText()))
                .build();
            
            AlertEvent event = AlertEvent.builder()
                .eventType(eventType)
                .alert(alert)
                .build();
            
            logger.with("operation", "consumeAlertEvent")
                  .with("eventType", eventType)
                  .with("alertId", alert.getId())
                  .with("deviceId", alert.getDeviceId())
                  .info("Consumed alert lifecycle event");
            
            // Publish to GraphQL subscriptions
            subscriptionResolver.publishAlertEvent(event);
            
        } catch (Exception e) {
            logger.with("operation", "consumeAlertEvent")
                  .with("message", message)
                  .error("Failed to process alert lifecycle event", e);
        }
    }
}