package io.thatworked.support.ping.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePingTargetDTO {
    @NotNull(message = "Device ID is required")
    private UUID deviceId;

    private String ipAddress;
    
    private String hostname;

    private Integer pingIntervalSeconds;
}