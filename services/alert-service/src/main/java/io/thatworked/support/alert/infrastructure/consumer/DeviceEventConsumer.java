package io.thatworked.support.alert.infrastructure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.alert.application.usecase.DeleteAlertsByDeviceIdUseCase;
import io.thatworked.support.alert.config.EventConfig;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class DeviceEventConsumer {

    private final StructuredLogger logger;
    private final DeleteAlertsByDeviceIdUseCase deleteAlertsByDeviceIdUseCase;
    private final ObjectMapper objectMapper;
    private final EventConfig eventConfig;
    
    public DeviceEventConsumer(StructuredLoggerFactory loggerFactory,
                              DeleteAlertsByDeviceIdUseCase deleteAlertsByDeviceIdUseCase,
                              ObjectMapper objectMapper,
                              EventConfig eventConfig) {
        this.logger = loggerFactory.getLogger(DeviceEventConsumer.class);
        this.deleteAlertsByDeviceIdUseCase = deleteAlertsByDeviceIdUseCase;
        this.objectMapper = objectMapper;
        this.eventConfig = eventConfig;
    }

    @KafkaListener(topics = "${alert-service.kafka.topics.device-events}", groupId = "${alert-service.kafka.consumer.group-id}")
    public void handleDeviceEvent(@Payload String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                 @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.with("topic", topic)
              .with("partition", partition)
              .with("offset", offset)
              .debug("Received device event");
        
        try {
            // Parse JSON string to Map
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            
            String eventType = (String) event.get(eventConfig.getFieldName("event-type"));
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) event.get(eventConfig.getFieldName("payload"));
            
            if (eventConfig.getType("device-deleted").equals(eventType) && payload != null) {
                String deviceIdStr = (String) payload.get(eventConfig.getFieldName("device-id"));
                if (deviceIdStr != null) {
                    UUID deviceId = UUID.fromString(deviceIdStr);
                    
                    // Delete all alerts for this device
                    int deletedCount = deleteAlertsByDeviceIdUseCase.execute(deviceId);
                    
                    logger.with("deletedCount", deletedCount)
                          .with("deviceId", deviceId)
                          .info("Deleted alerts for device");
                }
            }
            
        } catch (Exception e) {
            logger.with("message", message)
                  .error("Failed to process device event", e);
        }
    }
}