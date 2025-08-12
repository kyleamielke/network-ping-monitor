package io.thatworked.support.gateway.resolver;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.dto.alert.AlertDTO;
import io.thatworked.support.gateway.dto.ping.PingResultDTO;
import io.thatworked.support.gateway.dto.ping.PingUpdateDTO;
import io.thatworked.support.gateway.dto.dashboard.DeviceStatusDTO;
import io.thatworked.support.gateway.event.AlertEvent;
import io.thatworked.support.gateway.event.PingEvent;
import io.thatworked.support.gateway.event.DeviceStatusEvent;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class SubscriptionResolver {
    
    private final StructuredLogger logger;
    
    // Sinks for different event types - using replay(1) to ensure late subscribers get the latest event
    private final Sinks.Many<PingEvent> pingEventSink;
    private final Sinks.Many<AlertEvent> alertEventSink;
    private final Sinks.Many<DeviceStatusEvent> deviceStatusEventSink;
    
    // Device-specific ping sinks
    private final Map<UUID, Sinks.Many<PingEvent>> devicePingSinks = new ConcurrentHashMap<>();
    
    // Device-specific status sinks
    private final Map<UUID, Sinks.Many<DeviceStatusEvent>> deviceStatusSinks = new ConcurrentHashMap<>();
    
    public SubscriptionResolver(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(SubscriptionResolver.class);
        
        // Create replay sinks that keep the latest event for late subscribers
        this.pingEventSink = Sinks.many().replay().latest();
        this.alertEventSink = Sinks.many().replay().latest();
        this.deviceStatusEventSink = Sinks.many().replay().latest();
        
        logger.with("operation", "init")
              .info("Initialized subscription resolver with event sinks");
    }
    
    @SubscriptionMapping
    public Flux<PingUpdateDTO> pingUpdates(@Argument String deviceId) {
        if (deviceId == null) {
            // Subscribe to all ping updates
            logger.with("operation", "pingUpdates")
                  .with("subscription", "pingUpdates")
                  .with("scope", "all")
                  .with("subscriberCount", pingEventSink.currentSubscriberCount())
                  .info("Client subscribed to all ping updates");
            
            // Log sink details
            logger.with("operation", "pingUpdates")
                  .with("sinkClass", pingEventSink.getClass().getName())
                  .with("sinkHashCode", System.identityHashCode(pingEventSink))
                  .with("currentSubscriberCount", pingEventSink.currentSubscriberCount())
                  .info("Creating subscription from sink");
            
            // Return the flux directly without any bullshit
            return pingEventSink.asFlux()
                .map(this::convertPingEventToDTO)
                .doOnCancel(() -> logger.with("operation", "pingUpdates")
                                       .with("subscription", "pingUpdates")
                                       .with("scope", "all")
                                       .with("subscriberCount", pingEventSink.currentSubscriberCount())
                                       .info("Client unsubscribed from all ping updates"))
                .doOnSubscribe(subscription -> {
                    logger.with("operation", "pingUpdates")
                          .with("subscription", "pingUpdates")
                          .with("scope", "all")
                          .with("subscriptionId", subscription.toString())
                          .with("subscriberCountAfterActivation", pingEventSink.currentSubscriberCount())
                          .info("Subscription activated for all ping updates");
                })
                .doOnNext(event -> logger.with("operation", "pingUpdates")
                                        .with("subscription", "pingUpdates")
                                        .with("scope", "all")
                                        .with("deviceId", event.getDeviceId())
                                        .with("success", event.isSuccess())
                                        .info("Ping event sent to subscription"))
                .doOnError(error -> logger.with("operation", "pingUpdates")
                                         .with("subscription", "pingUpdates")
                                         .with("scope", "all")
                                         .with("errorType", error.getClass().getSimpleName())
                                         .with("errorMessage", error.getMessage())
                                         .error("Error in ping updates subscription", error));
        } else {
            // Subscribe to specific device ping updates
            try {
                UUID uuid = UUID.fromString(deviceId);
                logger.with("operation", "pingUpdates")
                      .with("subscription", "pingUpdates")
                      .with("scope", "device")
                      .with("deviceId", deviceId)
                      .info("Client subscribed to device ping updates");
            
            Sinks.Many<PingEvent> deviceSink = devicePingSinks.computeIfAbsent(uuid, 
                k -> Sinks.many().multicast().onBackpressureBuffer(100));
            
            return deviceSink.asFlux()
                .map(this::convertPingEventToDTO)
                .doOnCancel(() -> {
                    logger.with("operation", "pingUpdates")
                          .with("subscription", "pingUpdates")
                          .with("scope", "device")
                          .with("deviceId", deviceId)
                          .info("Client unsubscribed from device ping updates");
                    // Clean up device sink if no more subscribers
                    if (deviceSink.currentSubscriberCount() == 0) {
                        devicePingSinks.remove(uuid);
                    }
                })
                .doOnError(error -> logger.with("operation", "pingUpdates")
                                         .with("subscription", "pingUpdates")
                                         .with("scope", "device")
                                         .with("deviceId", deviceId)
                                         .with("errorType", error.getClass().getSimpleName())
                                         .with("errorMessage", error.getMessage())
                                         .error("Error in device ping updates subscription", error));
            } catch (IllegalArgumentException e) {
                logger.with("operation", "pingUpdates")
                      .with("subscription", "pingUpdates")
                      .with("deviceId", deviceId)
                      .with("errorType", "INVALID_UUID")
                      .error("Invalid device ID format", e);
                throw new IllegalArgumentException("Invalid device ID format: " + deviceId, e);
            }
        }
    }
    
    @SubscriptionMapping
    public Flux<AlertDTO> alertStream() {
        logger.with("operation", "alertStream")
              .with("subscription", "alertStream")
              .with("subscriberCount", alertEventSink.currentSubscriberCount())
              .info("Client subscribed to alert stream");
        
        return alertEventSink.asFlux()
            .mergeWith(Flux.<AlertEvent>never()) // Keep the stream alive
            .map(AlertEvent::getAlert)
            .doOnCancel(() -> logger.with("operation", "alertStream")
                                   .with("subscription", "alertStream")
                                   .with("subscriberCount", alertEventSink.currentSubscriberCount())
                                   .info("Client unsubscribed from alert stream"))
            .doOnSubscribe(subscription -> {
                logger.with("operation", "alertStream")
                      .with("subscription", "alertStream")
                      .with("subscriptionId", subscription.toString())
                      .with("subscriberCountAfterActivation", alertEventSink.currentSubscriberCount())
                      .info("Alert stream subscription activated");
            })
            .doOnNext(alert -> logger.with("operation", "alertStream")
                                    .with("subscription", "alertStream")
                                    .with("alertId", alert.getId())
                                    .with("deviceId", alert.getDeviceId())
                                    .info("Alert sent to subscription"))
            .doOnError(error -> logger.with("operation", "alertStream")
                                     .with("subscription", "alertStream")
                                     .with("errorType", error.getClass().getSimpleName())
                                     .with("errorMessage", error.getMessage())
                                     .error("Error in alert stream subscription", error));
    }
    
    @SubscriptionMapping
    public Flux<Map<String, Object>> deviceStatusUpdates(@Argument String deviceId) {
        if (deviceId == null) {
            // Subscribe to all device status updates
            logger.with("operation", "deviceStatusUpdates")
                  .with("subscription", "deviceStatusUpdates")
                  .with("scope", "all")
                  .info("Client subscribed to all device status updates");
            
            return deviceStatusEventSink.asFlux()
                .mergeWith(Flux.never()) // Keep the stream alive
                .map(this::convertStatusEventToMap)
                .doOnCancel(() -> logger.with("operation", "deviceStatusUpdates")
                                       .with("subscription", "deviceStatusUpdates")
                                       .with("scope", "all")
                                       .info("Client unsubscribed from all device status updates"))
                .doOnError(error -> logger.with("operation", "deviceStatusUpdates")
                                         .with("subscription", "deviceStatusUpdates")
                                         .with("scope", "all")
                                         .with("errorType", error.getClass().getSimpleName())
                                         .with("errorMessage", error.getMessage())
                                         .error("Error in device status updates subscription", error));
        } else {
            // Subscribe to specific device status updates
            try {
                UUID uuid = UUID.fromString(deviceId);
                logger.with("operation", "deviceStatusUpdates")
                      .with("subscription", "deviceStatusUpdates")
                      .with("scope", "device")
                      .with("deviceId", deviceId)
                      .info("Client subscribed to device status updates");
            
            Sinks.Many<DeviceStatusEvent> deviceSink = deviceStatusSinks.computeIfAbsent(uuid,
                k -> Sinks.many().multicast().onBackpressureBuffer(100));
            
            return deviceSink.asFlux()
                .mergeWith(Flux.never()) // Keep the stream alive
                .map(this::convertStatusEventToMap)
                .doOnCancel(() -> {
                    logger.with("operation", "deviceStatusUpdates")
                          .with("subscription", "deviceStatusUpdates")
                          .with("scope", "device")
                          .with("deviceId", deviceId)
                          .info("Client unsubscribed from device status updates");
                    // Clean up device sink if no more subscribers
                    if (deviceSink.currentSubscriberCount() == 0) {
                        deviceStatusSinks.remove(uuid);
                    }
                })
                .doOnError(error -> logger.with("operation", "deviceStatusUpdates")
                                         .with("subscription", "deviceStatusUpdates")
                                         .with("scope", "device")
                                         .with("deviceId", deviceId)
                                         .with("errorType", error.getClass().getSimpleName())
                                         .with("errorMessage", error.getMessage())
                                         .error("Error in device status updates subscription", error));
            } catch (IllegalArgumentException e) {
                logger.with("operation", "deviceStatusUpdates")
                      .with("subscription", "deviceStatusUpdates")
                      .with("deviceId", deviceId)
                      .with("errorType", "INVALID_UUID")
                      .error("Invalid device ID format", e);
                throw new IllegalArgumentException("Invalid device ID format: " + deviceId, e);
            }
        }
    }
    
    // Methods to publish events (called by Kafka consumers)
    
    public void publishPingEvent(PingEvent event) {
        try {
            logger.with("operation", "publishPingEvent")
                  .with("eventType", "ping")
                  .with("deviceId", event.getDeviceId())
                  .with("success", event.isSuccess())
                  .with("responseTimeMs", event.getResponseTimeMs())
                  .with("globalSinkSubscribers", pingEventSink.currentSubscriberCount())
                  .info("Publishing ping event to subscription sinks");
            
            // Publish to global sink
            logger.with("operation", "publishPingEvent")
                  .with("sinkClass", pingEventSink.getClass().getName())
                  .with("currentSubscriberCount", pingEventSink.currentSubscriberCount())
                  .info("About to emit to sink");
            
            Sinks.EmitResult globalResult = pingEventSink.tryEmitNext(event);
            
            logger.with("operation", "publishPingEvent")
                  .with("emitResult", globalResult.toString())
                  .with("isSuccess", globalResult.isSuccess())
                  .with("isFailure", globalResult.isFailure())
                  .info("Emit result");
            
            if (globalResult.isFailure()) {
                logger.with("operation", "publishPingEvent")
                      .with("eventType", "ping")
                      .with("deviceId", event.getDeviceId())
                      .with("sink", "global")
                      .with("emitResult", globalResult)
                      .warn("Failed to emit ping event to global sink");
            }
            
            // Publish to device-specific sink if exists
            UUID deviceId = event.getDeviceId();
            Sinks.Many<PingEvent> deviceSink = devicePingSinks.get(deviceId);
            if (deviceSink != null) {
                Sinks.EmitResult deviceResult = deviceSink.tryEmitNext(event);
                if (deviceResult.isFailure()) {
                    logger.with("operation", "publishPingEvent")
                          .with("eventType", "ping")
                          .with("deviceId", event.getDeviceId())
                          .with("sink", "device")
                          .with("emitResult", deviceResult)
                          .warn("Failed to emit ping event to device sink");
                }
            }
        } catch (Exception e) {
            logger.with("operation", "publishPingEvent")
                  .with("eventType", "ping")
                  .with("deviceId", event.getDeviceId())
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("Error publishing ping event", e);
        }
    }
    
    public void publishAlertEvent(AlertEvent event) {
        try {
            logger.with("operation", "publishAlertEvent")
                  .with("eventType", "alert")
                  .with("alertId", event.getAlert().getId())
                  .with("deviceId", event.getAlert().getDeviceId())
                  .with("alertType", event.getAlert().getAlertType())
                  .with("resolved", event.getAlert().isResolved())
                  .info("Publishing alert event");
            
            Sinks.EmitResult result = alertEventSink.tryEmitNext(event);
            if (result.isFailure()) {
                logger.with("operation", "publishAlertEvent")
                      .with("eventType", "alert")
                      .with("alertId", event.getAlert().getId())
                      .with("emitResult", result)
                      .warn("Failed to emit alert event");
            }
        } catch (Exception e) {
            logger.with("operation", "publishAlertEvent")
                  .with("eventType", "alert")
                  .with("alertId", event.getAlert().getId())
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("Error publishing alert event", e);
        }
    }
    
    public void publishDeviceStatusEvent(DeviceStatusEvent event) {
        try {
            logger.with("operation", "publishDeviceStatusEvent")
                  .with("eventType", "deviceStatus")
                  .with("deviceId", event.getDeviceId())
                  .with("online", event.isOnline())
                  .with("consecutiveFailures", event.getConsecutiveFailures())
                  .debug("Publishing device status event");
            
            // Publish to global sink
            Sinks.EmitResult globalResult = deviceStatusEventSink.tryEmitNext(event);
            if (globalResult.isFailure()) {
                logger.with("operation", "publishDeviceStatusEvent")
                      .with("eventType", "deviceStatus")
                      .with("deviceId", event.getDeviceId())
                      .with("sink", "global")
                      .with("emitResult", globalResult)
                      .warn("Failed to emit device status event to global sink");
            }
            
            // Publish to device-specific sink if exists
            UUID deviceId = event.getDeviceId();
            Sinks.Many<DeviceStatusEvent> deviceSink = deviceStatusSinks.get(deviceId);
            if (deviceSink != null) {
                Sinks.EmitResult deviceResult = deviceSink.tryEmitNext(event);
                if (deviceResult.isFailure()) {
                    logger.with("operation", "publishDeviceStatusEvent")
                          .with("eventType", "deviceStatus")
                          .with("deviceId", event.getDeviceId())
                          .with("sink", "device")
                          .with("emitResult", deviceResult)
                          .warn("Failed to emit device status event to device sink");
                }
            }
        } catch (Exception e) {
            logger.with("operation", "publishDeviceStatusEvent")
                  .with("eventType", "deviceStatus")
                  .with("deviceId", event.getDeviceId())
                  .with("errorType", e.getClass().getSimpleName())
                  .with("errorMessage", e.getMessage())
                  .error("Error publishing device status event", e);
        }
    }
    
    // Status method for debugging
    public String getSubscriptionStatus() {
        return String.format(
            "PingEventSink: %d subscribers, AlertEventSink: %d subscribers, DeviceStatusEventSink: %d subscribers, DevicePingSinks: %d, DeviceStatusSinks: %d",
            pingEventSink.currentSubscriberCount(),
            alertEventSink.currentSubscriberCount(),
            deviceStatusEventSink.currentSubscriberCount(),
            devicePingSinks.size(),
            deviceStatusSinks.size()
        );
    }
    
    // Helper methods to convert events to GraphQL response objects
    
    private PingUpdateDTO convertPingEventToDTO(PingEvent event) {
        PingUpdateDTO dto = PingUpdateDTO.builder()
            .deviceId(event.getDeviceId())
            .timestamp(event.getTimestamp())
            .success(event.isSuccess())
            .responseTimeMs(event.getResponseTimeMs())
            .previousStatus(event.isSuccess() ? "OFFLINE" : "ONLINE")
            .currentStatus(event.isSuccess() ? "ONLINE" : "OFFLINE")
            .build();
        
        logger.with("operation", "convertPingEventToDTO")
              .with("deviceId", event.getDeviceId())
              .with("success", event.isSuccess())
              .with("previousStatus", dto.getPreviousStatus())
              .with("currentStatus", dto.getCurrentStatus())
              .debug("Converted ping event to DTO");
        
        return dto;
    }
    
    private Map<String, Object> convertStatusEventToMap(DeviceStatusEvent event) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("deviceId", event.getDeviceId().toString());
        map.put("online", event.isOnline());
        map.put("lastSeenAt", event.getLastSeenAt());
        map.put("responseTimeMs", event.getResponseTimeMs());
        map.put("consecutiveFailures", event.getConsecutiveFailures());
        return map;
    }
}