package io.thatworked.support.alert.infrastructure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.alert.application.usecase.ProcessMonitoringEventUseCase;
import io.thatworked.support.alert.infrastructure.event.dto.MonitoringEvent;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class MonitoringEventConsumer {

    private final StructuredLogger logger;
    private final ProcessMonitoringEventUseCase processMonitoringEventUseCase;
    private final ObjectMapper objectMapper;
    
    public MonitoringEventConsumer(StructuredLoggerFactory loggerFactory,
                                  ProcessMonitoringEventUseCase processMonitoringEventUseCase,
                                  ObjectMapper objectMapper) {
        this.logger = loggerFactory.getLogger(MonitoringEventConsumer.class);
        this.processMonitoringEventUseCase = processMonitoringEventUseCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${alert-service.kafka.topics.ping-monitoring-events}", groupId = "${alert-service.kafka.consumer.group-id}")
    public void handlePingMonitoringEvent(@Payload String message,
                                         @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                         @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                         @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.with("topic", topic)
              .with("partition", partition)
              .with("offset", offset)
              .debug("Received ping monitoring event");
        
        try {
            // Parse JSON string directly to MonitoringEvent
            MonitoringEvent event = objectMapper.readValue(message, MonitoringEvent.class);
            
            // Ensure the source is set correctly
            if (event.getSource() == null) {
                event.setSource(MonitoringEvent.Source.PING_SERVICE);
            }
            
            processMonitoringEventUseCase.execute(
                event.getDeviceId(),
                event.getDeviceName(),
                event.getEventType().name(),
                event.getMessage(),
                event.getTimestamp(),
                event.getIpAddress(),
                event.getConsecutiveFailures(),
                event.getMessage() // Use message as failure reason for now
            );
            
            logger.with("deviceId", event.getDeviceId())
                  .debug("Successfully processed ping monitoring event");
                
        } catch (Exception e) {
            logger.with("message", message)
                  .error("Failed to process ping monitoring event", e);
            // Consider implementing dead letter queue or retry mechanism
        }
    }

}