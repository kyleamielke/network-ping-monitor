package io.thatworked.support.gateway.dto.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Device DTO matching the exact response from device-service.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceDTO {
    private UUID id;
    private Long version;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String lastModifiedBy;
    private String name;
    private String ipAddress;
    private String hostname;
    private String macAddress;
    private String type; // e.g. "SERVER"
    private String os;
    private String osType; // e.g. "OTHER"
    private String make;
    private String model;
    private String endpointId;
    private String assetTag;
    private String description;
    private String location;
    private Map<String, Object> metadata;
    private UUID site;
    private List<RoleDTO> roles;
}