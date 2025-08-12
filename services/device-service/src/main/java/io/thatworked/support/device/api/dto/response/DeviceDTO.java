package io.thatworked.support.device.api.dto.response;

import io.thatworked.support.device.api.dto.common.BaseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DeviceDTO extends BaseDTO {
    private String name;
    private String ipAddress;
    private String hostname;
    private String macAddress;
    private String type;
    private String os;
    private String osType;
    private String make;
    private String model;
    private String endpointId;
    private String assetTag;
    private String description;
    private String location;
    
    @Builder.Default
    private Map<String, String> metadata = new HashMap<>();
    
    private UUID site;  // Simple UUID reference to a site

    // Set of device roles
    @Builder.Default
    private Set<RoleDTO> roles = new HashSet<>();
}