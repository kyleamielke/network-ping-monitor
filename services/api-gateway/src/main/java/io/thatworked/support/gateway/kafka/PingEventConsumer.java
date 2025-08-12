package io.thatworked.support.gateway.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.event.PingEvent;
import io.thatworked.support.gateway.resolver.SubscriptionResolver;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PingEventConsumer {
    
    private final StructuredLogger logger;
    private final SubscriptionResolver subscriptionResolver;
    private final ObjectMapper objectMapper;
    
    public PingEventConsumer(StructuredLoggerFactory loggerFactory,
                            SubscriptionResolver subscriptionResolver,
                            ObjectMapper objectMapper) {
        this.logger = loggerFactory.getLogger(PingEventConsumer.class);
        this.subscriptionResolver = subscriptionResolver;
        this.objectMapper = objectMapper;
    }
    
    @KafkaListener(topics = "ping-results", groupId = "api-gateway")
    public void consumePingEvent(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            
            // Parse timestamp - it's a decimal number of seconds since epoch
            double timestampSeconds = jsonNode.get("timestamp").asDouble();
            long timestampMillis = (long)(timestampSeconds * 1000);
            Instant timestamp = Instant.ofEpochMilli(timestampMillis);
            
            PingEvent event = PingEvent.builder()
                .deviceId(UUID.fromString(jsonNode.get("deviceId").asText()))
                .timestamp(timestamp)
                .success(jsonNode.get("success").asBoolean())
                .responseTimeMs(jsonNode.has("responseTimeMs") && !jsonNode.get("responseTimeMs").isNull() 
                    ? jsonNode.get("responseTimeMs").asLong() : null)
                .errorMessage(jsonNode.has("errorMessage") && !jsonNode.get("errorMessage").isNull()
                    ? jsonNode.get("errorMessage").asText() : null)
                .build();
            
            logger.with("operation", "consumePingEvent")
                  .with("deviceId", event.getDeviceId())
                  .with("success", event.isSuccess())
                  .with("responseTimeMs", event.getResponseTimeMs())
                  .info("Consumed ping monitoring event from Kafka");
            
            // Publish to GraphQL subscriptions
            subscriptionResolver.publishPingEvent(event);
            
            logger.with("operation", "consumePingEvent")
                  .with("deviceId", event.getDeviceId())
                  .info("Published ping event to GraphQL subscriptions");
            
        } catch (Exception e) {
            logger.with("operation", "consumePingEvent")
                  .with("message", message)
                  .error("Failed to process ping monitoring event", e);
        }
    }
}