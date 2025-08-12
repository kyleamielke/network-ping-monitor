package io.thatworked.support.device.api.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceFilter {
    private String name;
    private String ipAddress;
    private String deviceType;
    private String os;
    private String make;
    private String model;
    private UUID siteId;
    private Boolean isAssigned;
    private String searchTerm; // For general search across multiple fields
}