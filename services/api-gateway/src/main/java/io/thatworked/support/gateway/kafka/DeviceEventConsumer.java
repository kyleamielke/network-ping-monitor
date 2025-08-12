package io.thatworked.support.gateway.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.event.DeviceStatusEvent;
import io.thatworked.support.gateway.resolver.SubscriptionResolver;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class DeviceEventConsumer {
    
    private final StructuredLogger logger;
    private final SubscriptionResolver subscriptionResolver;
    private final ObjectMapper objectMapper;
    
    public DeviceEventConsumer(StructuredLoggerFactory loggerFactory,
                              SubscriptionResolver subscriptionResolver,
                              ObjectMapper objectMapper) {
        this.logger = loggerFactory.getLogger(DeviceEventConsumer.class);
        this.subscriptionResolver = subscriptionResolver;
        this.objectMapper = objectMapper;
    }
    
    @KafkaListener(topics = "device-events", groupId = "api-gateway")
    public void consumeDeviceEvent(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            
            // Device events should have an eventType field
            if (!jsonNode.has("eventType")) {
                logger.with("operation", "consumeDeviceEvent")
                      .with("message", message)
                      .warn("Device event missing eventType field");
                return;
            }
            
            String eventType = jsonNode.get("eventType").asText();
            
            // For now, we're mainly interested in device status changes
            // which might come from ping results or explicit status updates
            if (eventType.contains("status") || eventType.contains("device")) {
                UUID deviceId = UUID.fromString(jsonNode.get("deviceId").asText());
                
                // Extract status information if available
                boolean online = jsonNode.has("online") ? jsonNode.get("online").asBoolean() : false;
                Instant lastSeenAt = jsonNode.has("lastSeenAt") && !jsonNode.get("lastSeenAt").isNull()
                    ? Instant.parse(jsonNode.get("lastSeenAt").asText()) : Instant.now();
                Long responseTimeMs = jsonNode.has("responseTimeMs") && !jsonNode.get("responseTimeMs").isNull()
                    ? jsonNode.get("responseTimeMs").asLong() : null;
                Integer consecutiveFailures = jsonNode.has("consecutiveFailures") 
                    ? jsonNode.get("consecutiveFailures").asInt() : 0;
                
                DeviceStatusEvent event = DeviceStatusEvent.builder()
                    .deviceId(deviceId)
                    .online(online)
                    .lastSeenAt(lastSeenAt)
                    .responseTimeMs(responseTimeMs)
                    .consecutiveFailures(consecutiveFailures)
                    .build();
                
                logger.with("operation", "consumeDeviceEvent")
                      .with("eventType", eventType)
                      .with("deviceId", deviceId)
                      .with("online", online)
                      .debug("Consumed device event");
                
                // Publish to GraphQL subscriptions
                subscriptionResolver.publishDeviceStatusEvent(event);
            }
            
        } catch (Exception e) {
            logger.with("operation", "consumeDeviceEvent")
                  .with("message", message)
                  .error("Failed to process device event", e);
        }
    }
}