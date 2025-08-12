package io.thatworked.support.ping.infrastructure.event.alert;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class DeviceAlertEvent {
    private UUID deviceId;
    private String deviceName;
    private String ipAddress;
    private Instant timestamp;
    private String sourceMicroservice;
    
    protected DeviceAlertEvent(UUID deviceId, String deviceName, String ipAddress) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.ipAddress = ipAddress;
        this.timestamp = Instant.now();
        this.sourceMicroservice = "ping-service";
    }
}