package io.thatworked.support.ping.infrastructure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.ping.application.PingApplicationService;
import io.thatworked.support.ping.application.service.MonitoredDeviceService;
import io.thatworked.support.ping.domain.MonitoredDevice;
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

    private final PingApplicationService pingApplicationService;
    private final MonitoredDeviceService monitoredDeviceService;
    private final ObjectMapper objectMapper;
    private final io.thatworked.support.common.logging.StructuredLogger logger;
    
    public DeviceEventConsumer(PingApplicationService pingApplicationService,
                             MonitoredDeviceService monitoredDeviceService,
                             ObjectMapper objectMapper,
                             StructuredLoggerFactory structuredLoggerFactory) {
        this.pingApplicationService = pingApplicationService;
        this.monitoredDeviceService = monitoredDeviceService;
        this.objectMapper = objectMapper;
        this.logger = structuredLoggerFactory.getLogger(DeviceEventConsumer.class);
    }

    @KafkaListener(topics = "device-events", groupId = "ping-service")
    public void handleDeviceEvent(@Payload String message,
                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                 @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.with("eventType", "received")
              .with("topic", topic)
              .with("partition", partition)
              .with("offset", offset)
              .debug("Received device event");
        
        try {
            // Parse JSON string to Map
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            
            String eventType = (String) event.get("eventType");
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = (Map<String, Object>) event.get("payload");
            
            if (payload != null) {
                String deviceIdStr = (String) payload.get("deviceId");
                if (deviceIdStr != null) {
                    UUID deviceId = UUID.fromString(deviceIdStr);
                    
                    switch (eventType) {
                        case "device.created":
                        case "device.updated":
                            handleDeviceCreateOrUpdate(deviceId, payload);
                            break;
                        case "device.deleted":
                            handleDeviceDeleted(deviceId);
                            break;
                        default:
                            logger.with("eventType", eventType)
                                  .with("deviceId", deviceId)
                                  .debug("Ignoring unhandled device event type");
                    }
                }
            }
            
        } catch (Exception e) {
            logger.with("eventType", "error")
                  .with("message", message)
                  .error("Failed to process device event", e);
        }
    }
    
    private void handleDeviceCreateOrUpdate(UUID deviceId, Map<String, Object> payload) {
        String deviceName = (String) payload.get("deviceName");
        
        // Handle IP address - could be a string (create) or a map (update)
        String ipAddress = extractStringValue(payload.get("ipAddress"));
        
        // Handle hostname - could be a string (create) or a map (update)
        String hostname = extractStringValue(payload.get("hostname"));
        
        // Handle both direct values and update maps
        String os = extractStringValue(payload.get("os"));
        String osType = extractStringValue(payload.get("osType"));
        
        Object siteObj = payload.get("siteId");
        UUID site = null;
        if (siteObj instanceof String && !((String) siteObj).isEmpty()) {
            site = UUID.fromString((String) siteObj);
        }
        
        logger.with("businessEvent", "deviceCreateOrUpdate")
              .with("deviceId", deviceId)
              .with("deviceName", deviceName)
              .with("ipAddress", ipAddress)
              .with("hostname", hostname)
              .info("Processing device create/update event");
        
        MonitoredDevice device = new MonitoredDevice();
        device.setDeviceId(deviceId);
        device.setDeviceName(deviceName);
        device.setIpAddress(ipAddress);
        device.setOs(os);
        device.setOsType(osType);
        device.setSite(site);
        
        monitoredDeviceService.save(device);
        
        // Update the ping target IP address and hostname if it exists
        try {
            pingApplicationService.updatePingTargetAddress(deviceId, ipAddress, hostname);
            logger.with("businessEvent", "pingTargetUpdated")
                  .with("deviceId", deviceId)
                  .with("ipAddress", ipAddress)
                  .with("hostname", hostname)
                  .info("Updated ping target address");
        } catch (Exception e) {
            logger.with("businessEvent", "pingTargetUpdateFailed")
                  .with("deviceId", deviceId)
                  .with("ipAddress", ipAddress)
                  .with("hostname", hostname)
                  .with("error", e.getMessage())
                  .debug("Failed to update ping target - may not exist");
        }
        
        logger.with("businessEvent", "deviceSaved")
              .with("deviceId", deviceId)
              .info("Device information saved to local storage");
    }
    
    private void handleDeviceDeleted(UUID deviceId) {
        logger.with("businessEvent", "deviceDeleted")
              .with("deviceId", deviceId)
              .info("Processing device deletion");
        
        // Delete from monitored devices
        monitoredDeviceService.deleteById(deviceId);
        
        // Delete ping target and all associated data for this device
        pingApplicationService.cleanupDeviceData(deviceId);
        
        logger.with("businessEvent", "deviceDataCleanupCompleted")
              .with("deviceId", deviceId)
              .info("Device data cleanup completed");
    }
    
    private String extractStringValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        }
        if (value instanceof Map) {
            // For update events, get the "new" value
            Map<?, ?> map = (Map<?, ?>) value;
            Object newValue = map.get("new");
            return newValue != null ? newValue.toString() : null;
        }
        return value.toString();
    }
}