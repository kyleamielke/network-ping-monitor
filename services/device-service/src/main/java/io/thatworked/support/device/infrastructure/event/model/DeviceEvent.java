package io.thatworked.support.device.infrastructure.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEvent {
    private UUID deviceId;
    private String deviceName;
    private String eventType;  // CREATED, UPDATED, DELETED, ASSIGNED, UNASSIGNED
    private UUID siteId;  // Optional, for assignment events
    private OffsetDateTime timestamp;
    private String deviceType;
    private String ipAddress;
    private String hostname;
    @Singular("metadata")
    private Map<String, Object> metadata;

    public static DeviceEvent created(UUID deviceId, String deviceName, String deviceType) {
        return DeviceEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .deviceType(deviceType)
                .eventType("CREATED")
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static DeviceEvent updated(UUID deviceId, String deviceName) {
        return DeviceEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .eventType("UPDATED")
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static DeviceEvent deleted(UUID deviceId, String deviceName) {
        return DeviceEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .eventType("DELETED")
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static DeviceEvent assigned(UUID deviceId, String deviceName, UUID siteId) {
        return DeviceEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .eventType("ASSIGNED")
                .siteId(siteId)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static DeviceEvent unassigned(UUID deviceId, String deviceName, UUID previousSiteId) {
        return DeviceEvent.builder()
                .deviceId(deviceId)
                .deviceName(deviceName)
                .eventType("UNASSIGNED")
                .siteId(previousSiteId)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}