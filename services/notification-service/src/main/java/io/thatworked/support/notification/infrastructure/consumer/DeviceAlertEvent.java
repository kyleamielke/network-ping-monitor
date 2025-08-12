package io.thatworked.support.notification.infrastructure.consumer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class DeviceAlertEvent {
    private UUID deviceId;
    private String deviceName;
    private String ipAddress;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private Instant timestamp;
    private String sourceMicroservice;
}