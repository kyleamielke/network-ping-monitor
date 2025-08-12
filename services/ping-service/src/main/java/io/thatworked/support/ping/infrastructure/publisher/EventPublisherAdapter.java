package io.thatworked.support.ping.infrastructure.publisher;

import io.thatworked.support.ping.infrastructure.config.KafkaConfig;
import io.thatworked.support.ping.domain.model.PingResultDomain;
import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.port.EventPublisher;
import io.thatworked.support.ping.api.dto.MonitoringEvent;
import io.thatworked.support.ping.infrastructure.event.PingTargetStartedEvent;
import io.thatworked.support.ping.infrastructure.event.PingTargetStoppedEvent;
import io.thatworked.support.ping.infrastructure.event.PingTargetIpUpdatedEvent;
import io.thatworked.support.ping.infrastructure.event.alert.DeviceDownEvent;
import io.thatworked.support.ping.infrastructure.event.alert.DeviceRecoveredEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Infrastructure adapter for event publishing.
 */
@Component
public class EventPublisherAdapter implements EventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;
    
    public EventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate,
                               ApplicationEventPublisher applicationEventPublisher) {
        this.kafkaTemplate = kafkaTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
    }
    
    @Override
    public void publishPingResult(PingResultDomain pingResult) {
        // NOTE: Individual ping results are NOT published directly.
        // Only significant state changes (DEVICE_DOWN, DEVICE_RECOVERED) are published
        // by AlertStateService after evaluating consecutive failures/successes.
        // This prevents flooding the event stream with individual ping results.
        
        // This method is kept for interface compatibility but does nothing.
        // Real monitoring events are published by AlertStateService.
    }
    
    @Override
    public void publishPingTargetStarted(PingTargetDomain pingTarget) {
        // Convert domain to entity for event
        var entity = io.thatworked.support.ping.domain.PingTarget.builder()
            .deviceId(pingTarget.getDeviceId())
            .ipAddress(pingTarget.getIpAddress())
            .hostname(pingTarget.getHostname())
            .isMonitored(pingTarget.isMonitored())
            .pingIntervalSeconds(pingTarget.getPingIntervalSeconds())
            .createdAt(pingTarget.getCreatedAt())
            .updatedAt(pingTarget.getUpdatedAt())
            .build();
            
        applicationEventPublisher.publishEvent(new PingTargetStartedEvent(entity));
    }
    
    @Override
    public void publishPingTargetStopped(PingTargetDomain pingTarget) {
        applicationEventPublisher.publishEvent(new PingTargetStoppedEvent(pingTarget.getDeviceId()));
    }
    
    @Override
    public void publishDeviceDown(UUID deviceId, String deviceName, String ipAddress) {
        DeviceDownEvent event = DeviceDownEvent.builder()
            .deviceId(deviceId)
            .deviceName(deviceName)
            .ipAddress(ipAddress)
            .timestamp(Instant.now())
            .sourceMicroservice("ping-service")
            .consecutiveFailures(1)
            .lastSuccessTime(null)
            .failureReason("Ping timeout")
            .build();
        
        kafkaTemplate.send(KafkaConfig.DEVICE_DOWN_TOPIC, event);
    }
    
    @Override
    public void publishDeviceRecovered(UUID deviceId, String deviceName, String ipAddress) {
        DeviceRecoveredEvent event = DeviceRecoveredEvent.builder()
            .deviceId(deviceId)
            .deviceName(deviceName)
            .ipAddress(ipAddress)
            .timestamp(Instant.now())
            .sourceMicroservice("ping-service")
            .downSince(null)
            .currentResponseTimeMs(0.0)
            .consecutiveSuccesses(1)
            .build();
        
        kafkaTemplate.send(KafkaConfig.DEVICE_RECOVERED_TOPIC, event);
    }
    
    @Override
    public void publishPingTargetIpUpdated(PingTargetDomain pingTarget, String oldIpAddress) {
        // Convert domain to entity for event
        var entity = io.thatworked.support.ping.domain.PingTarget.builder()
            .deviceId(pingTarget.getDeviceId())
            .ipAddress(pingTarget.getIpAddress())
            .hostname(pingTarget.getHostname())
            .isMonitored(pingTarget.isMonitored())
            .pingIntervalSeconds(pingTarget.getPingIntervalSeconds())
            .createdAt(pingTarget.getCreatedAt())
            .updatedAt(pingTarget.getUpdatedAt())
            .build();
            
        applicationEventPublisher.publishEvent(new PingTargetIpUpdatedEvent(entity, oldIpAddress));
    }
    
    @Override
    public void publishPingTargetAddressUpdated(PingTargetDomain pingTarget, String oldIpAddress, String oldHostname) {
        // Convert domain to entity for event
        var entity = io.thatworked.support.ping.domain.PingTarget.builder()
            .deviceId(pingTarget.getDeviceId())
            .ipAddress(pingTarget.getIpAddress())
            .hostname(pingTarget.getHostname())
            .isMonitored(pingTarget.isMonitored())
            .pingIntervalSeconds(pingTarget.getPingIntervalSeconds())
            .createdAt(pingTarget.getCreatedAt())
            .updatedAt(pingTarget.getUpdatedAt())
            .build();
            
        // For now, reuse the IP updated event - later we can create a specific address update event
        applicationEventPublisher.publishEvent(new PingTargetIpUpdatedEvent(entity, oldIpAddress));
    }
}