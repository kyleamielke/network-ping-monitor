package io.thatworked.support.ping.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class DeviceDTO {
    private UUID id;
    private String name;
    private String ipAddress;
    private String os;
    private String osType;
    private UUID site;
}