package io.thatworked.support.device.api.dto.request;

import io.thatworked.support.common.validation.constraints.ValidIpAddress;
import io.thatworked.support.common.validation.constraints.ValidMacAddress;
import io.thatworked.support.device.api.validation.ValidDeviceType;
import io.thatworked.support.device.api.validation.ValidDeviceAddress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidDeviceAddress
public class DeviceCreateRequest {
    
    @NotBlank(message = "Device name is required")
    @Size(min = 3, max = 100, message = "Device name must be between 3 and 100 characters")
    private String name;
    
    @ValidIpAddress
    private String ipAddress;
    
    @Size(max = 253, message = "Hostname cannot exceed 253 characters")
    private String hostname;
    
    @ValidMacAddress
    private String macAddress;
    
    @ValidDeviceType
    private String type;
    
    @Size(max = 100, message = "OS cannot exceed 100 characters")
    private String os;
    
    @Size(max = 50, message = "OS Type cannot exceed 50 characters")
    private String osType;
    
    @Size(max = 100, message = "Make cannot exceed 100 characters")
    private String make;
    
    @Size(max = 100, message = "Model cannot exceed 100 characters")
    private String model;
    
    @Size(max = 100, message = "Endpoint ID cannot exceed 100 characters")
    private String endpointId;
    
    @Size(max = 100, message = "Asset tag cannot exceed 100 characters")
    private String assetTag;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private String location;
    
    private Map<String, String> metadata;
    
    private Set<Long> roleIds;
}