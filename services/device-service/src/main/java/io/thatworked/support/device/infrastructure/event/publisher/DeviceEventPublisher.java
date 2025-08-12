package io.thatworked.support.device.infrastructure.event.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.device.infrastructure.entity.Device;
import io.thatworked.support.device.infrastructure.event.model.DeviceEvent;
import io.thatworked.support.device.config.properties.DeviceServiceProperties;
// import io.thatworked.support.device.infrastructure.service.FeatureFlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service for publishing device lifecycle events to Kafka
 * Other services can subscribe to these events to maintain their own local device data
 */
@Service
public class DeviceEventPublisher {
    
    private final StructuredLogger logger;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final DeviceServiceProperties properties;
    
    public DeviceEventPublisher(StructuredLoggerFactory loggerFactory,
                               KafkaTemplate<String, Object> kafkaTemplate,
                               ObjectMapper objectMapper,
                               DeviceServiceProperties properties) {
        this.logger = loggerFactory.getLogger(DeviceEventPublisher.class);
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }
    
    // @Autowired
    // private FeatureFlagService featureFlagService;
    
    /**
     * Publish device created event
     */
    public void publishDeviceCreated(Device device) {
        // if (featureFlagService.isAsyncEventsEnabled()) {
        //     publishDeviceCreatedAsync(device);
        // } else {
            publishDeviceCreatedSync(device);
        // }
    }
    
    @Async
    public CompletableFuture<Void> publishDeviceCreatedAsync(Device device) {
        publishDeviceCreatedSync(device);
        return CompletableFuture.completedFuture(null);
    }
    
    private void publishDeviceCreatedSync(Device device) {
        if (!properties.getKafka().isEnabled()) {
            logger.with("deviceId", device.getId())
                    .debug("Kafka events disabled, skipping device created event");
            return;
        }
        
        try {
            Map<String, Object> eventPayload = Map.of(
                "deviceId", device.getId(),
                "deviceName", device.getName(),
                "ipAddress", device.getIpAddress() != null ? device.getIpAddress() : "",
                "hostname", device.getHostname() != null ? device.getHostname() : "",
                "os", device.getOs() != null ? device.getOs() : "",
                "osType", device.getOsType() != null ? device.getOsType() : "",
                "site", device.getSite() != null ? device.getSite() : "",
                "timestamp", Instant.now()
            );
            
            Map<String, Object> event = Map.of(
                "eventType", "device.created",
                "payload", eventPayload
            );
            
            String topic = properties.getKafka().getTopic().getDeviceEvents();
            kafkaTemplate.send(topic, device.getId().toString(), event);
            logger.with("deviceId", device.getId())
                    .with("deviceName", device.getName())
                    .with("ipAddress", device.getIpAddress())
                    .info("Published device created event");
            
        } catch (Exception e) {
            logger.with("deviceId", device.getId())
                    .error("Failed to publish device created event", e);
        }
    }
    
    /**
     * Publish device updated event
     */
    public void publishDeviceUpdated(Device device, Map<String, Object> changes) {
        try {
            Map<String, Object> eventPayload = new HashMap<>();
            eventPayload.put("deviceId", device.getId());
            eventPayload.put("deviceName", device.getName());
            eventPayload.put("ipAddress", device.getIpAddress());
            eventPayload.put("hostname", device.getHostname());
            eventPayload.put("os", device.getOs() != null ? device.getOs() : "");
            eventPayload.put("osType", device.getOsType() != null ? device.getOsType() : "");
            eventPayload.put("site", device.getSite() != null ? device.getSite() : null);
            eventPayload.put("changes", changes);
            eventPayload.put("timestamp", Instant.now());
            
            Map<String, Object> event = Map.of(
                "eventType", "device.updated",
                "payload", eventPayload
            );
            
            String topic = properties.getKafka().getTopic().getDeviceEvents();
            kafkaTemplate.send(topic, device.getId().toString(), event);
            logger.with("deviceId", device.getId())
                    .with("deviceName", device.getName())
                    .with("ipAddress", device.getIpAddress())
                    .with("changes", changes)
                    .info("Published device updated event");
            
        } catch (Exception e) {
            logger.with("deviceId", device.getId())
                    .error("Failed to publish device updated event", e);
        }
    }
    
    /**
     * Publish device deleted event
     */
    public void publishDeviceDeleted(UUID deviceId) {
        try {
            Map<String, Object> eventPayload = Map.of(
                "deviceId", deviceId,
                "timestamp", Instant.now()
            );
            
            Map<String, Object> event = Map.of(
                "eventType", "device.deleted",
                "payload", eventPayload
            );
            
            String topic = properties.getKafka().getTopic().getDeviceEvents();
            kafkaTemplate.send(topic, deviceId.toString(), event);
            logger.with("deviceId", deviceId)
                    .info("Published device deleted event");
            
        } catch (Exception e) {
            logger.with("deviceId", deviceId)
                    .error("Failed to publish device deleted event", e);
        }
    }
    
    /**
     * Generic method to publish DeviceEvent objects
     */
    public void publishDeviceEvent(DeviceEvent deviceEvent) {
        try {
            Map<String, Object> eventPayload = new HashMap<>();
            eventPayload.put("deviceId", deviceEvent.getDeviceId());
            eventPayload.put("deviceName", deviceEvent.getDeviceName());
            eventPayload.put("ipAddress", deviceEvent.getIpAddress());
            eventPayload.put("hostname", deviceEvent.getHostname());
            eventPayload.put("deviceType", deviceEvent.getDeviceType());
            eventPayload.put("siteId", deviceEvent.getSiteId());
            eventPayload.put("timestamp", deviceEvent.getTimestamp());
            if (deviceEvent.getMetadata() != null) {
                eventPayload.putAll(deviceEvent.getMetadata());
            }
            
            Map<String, Object> event = Map.of(
                "eventType", deviceEvent.getEventType().toLowerCase().replace("_", "."),
                "payload", eventPayload
            );
            
            String topic = properties.getKafka().getTopic().getDeviceEvents();
            kafkaTemplate.send(topic, deviceEvent.getDeviceId().toString(), event);
            logger.with("eventType", deviceEvent.getEventType())
                    .with("deviceId", deviceEvent.getDeviceId())
                    .info("Published device event");
            
        } catch (Exception e) {
            logger.with("eventType", deviceEvent.getEventType())
                    .with("deviceId", deviceEvent.getDeviceId())
                    .error("Failed to publish device event", e);
        }
    }
}