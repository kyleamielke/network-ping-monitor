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
public class MonitoringEventConsumer {
    
    private final StructuredLogger logger;
    private final SubscriptionResolver subscriptionResolver;
    private final ObjectMapper objectMapper;
    
    public MonitoringEventConsumer(StructuredLoggerFactory loggerFactory,
                                  SubscriptionResolver subscriptionResolver,
                                  ObjectMapper objectMapper) {
        this.logger = loggerFactory.getLogger(MonitoringEventConsumer.class);
        this.subscriptionResolver = subscriptionResolver;
        this.objectMapper = objectMapper;
    }
    
    @KafkaListener(topics = "ping-monitoring-events", groupId = "api-gateway")
    public void consumeMonitoringEvent(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            
            String eventType = jsonNode.get("eventType").asText();
            
            // Only process device up/down events for status changes
            if ("DEVICE_DOWN".equals(eventType) || "DEVICE_UP".equals(eventType)) {
                // Parse timestamp - it's a decimal number of seconds since epoch
                double timestampSeconds = jsonNode.get("timestamp").asDouble();
                long timestampMillis = (long)(timestampSeconds * 1000);
                Instant timestamp = Instant.ofEpochMilli(timestampMillis);
                
                DeviceStatusEvent event = DeviceStatusEvent.builder()
                    .deviceId(UUID.fromString(jsonNode.get("deviceId").asText()))
                    .online("DEVICE_UP".equals(eventType))
                    .lastSeenAt(timestamp)
                    .responseTimeMs(jsonNode.has("responseTimeMs") && !jsonNode.get("responseTimeMs").isNull() 
                        ? jsonNode.get("responseTimeMs").asLong() : null)
                    .consecutiveFailures(jsonNode.has("consecutiveFailures") && !jsonNode.get("consecutiveFailures").isNull()
                        ? jsonNode.get("consecutiveFailures").asInt() : 0)
                    .build();
                
                logger.with("operation", "consumeMonitoringEvent")
                      .with("deviceId", event.getDeviceId())
                      .with("eventType", eventType)
                      .with("online", event.isOnline())
                      .with("consecutiveFailures", event.getConsecutiveFailures())
                      .info("Consumed device status change event from Kafka");
                
                // Publish to GraphQL subscriptions
                subscriptionResolver.publishDeviceStatusEvent(event);
                
                logger.with("operation", "consumeMonitoringEvent")
                      .with("deviceId", event.getDeviceId())
                      .info("Published device status event to GraphQL subscriptions");
            }
            
        } catch (Exception e) {
            logger.with("operation", "consumeMonitoringEvent")
                  .with("message", message)
                  .error("Failed to process monitoring event", e);
        }
    }
}