package io.thatworked.support.device.infrastructure.adapter;

import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.port.EventPublisher;
import io.thatworked.support.device.infrastructure.event.model.DeviceEvent;
import io.thatworked.support.device.infrastructure.event.publisher.DeviceEventPublisher;
import io.thatworked.support.device.infrastructure.event.constants.EventConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Adapter that implements the domain EventPublisher port using Kafka.
 */
@Component
@RequiredArgsConstructor
public class EventPublisherAdapter implements EventPublisher {
    
    private final DeviceEventPublisher kafkaEventPublisher;
    
    @Override
    public void publishDeviceCreated(DeviceDomain device) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_CREATED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .ipAddress(device.getIpAddress())
            .hostname(device.getHostname())
            .deviceType(device.getType())
            .siteId(device.getSiteId())
            .timestamp(OffsetDateTime.now())
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
    
    @Override
    public void publishDeviceUpdated(DeviceDomain device, Map<String, Object> changes) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_UPDATED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .ipAddress(device.getIpAddress())
            .hostname(device.getHostname())
            .deviceType(device.getType())
            .siteId(device.getSiteId())
            .timestamp(OffsetDateTime.now())
            .metadata(changes)
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
    
    @Override
    public void publishDeviceDeleted(DeviceDomain device) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_DELETED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .timestamp(OffsetDateTime.now())
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
    
    @Override
    public void publishDeviceActivated(DeviceDomain device) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_ACTIVATED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .timestamp(OffsetDateTime.now())
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
    
    @Override
    public void publishDeviceDeactivated(DeviceDomain device) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_DEACTIVATED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .timestamp(OffsetDateTime.now())
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
    
    @Override
    public void publishDeviceAssignedToSite(DeviceDomain device, UUID siteId) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_ASSIGNED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .siteId(siteId)
            .timestamp(OffsetDateTime.now())
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
    
    @Override
    public void publishDeviceRemovedFromSite(DeviceDomain device, UUID previousSiteId) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_UNASSIGNED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .siteId(previousSiteId)
            .timestamp(OffsetDateTime.now())
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
    
    @Override
    public void publishDeviceRolesUpdated(DeviceDomain device, List<String> roleNames) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_ROLES_UPDATED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .metadata("roles", String.join(",", roleNames))
            .timestamp(OffsetDateTime.now())
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
    
    @Override
    public void publishDeviceRolesCleared(DeviceDomain device, List<String> previousRoleNames) {
        DeviceEvent event = DeviceEvent.builder()
            .eventType(EventConstants.DEVICE_ROLES_CLEARED_EVENT)
            .deviceId(device.getId())
            .deviceName(device.getName())
            .metadata("previousRoles", String.join(",", previousRoleNames))
            .timestamp(OffsetDateTime.now())
            .build();
            
        kafkaEventPublisher.publishDeviceEvent(event);
    }
}