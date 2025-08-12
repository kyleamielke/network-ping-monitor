package io.thatworked.support.report.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.util.UUID;

/**
 * Infrastructure DTO for device data from device service.
 */
public class DeviceDTO {
    
    private UUID id;
    private String name;
    private String ipAddress;
    private String hostname;
    private String type;
    private String status;
    private String deviceType;
    private String macAddress;
    private boolean active;
    private boolean up;
    private String location;
    private String description;
    private String assetTag;
    private String endpointId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant updatedAt;
    
    // Constructors
    public DeviceDTO() {}
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getHostname() { return hostname; }
    public void setHostname(String hostname) { this.hostname = hostname; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    
    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
    
    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean isUp() { return up; }
    public void setUp(boolean up) { this.up = up; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAssetTag() { return assetTag; }
    public void setAssetTag(String assetTag) { this.assetTag = assetTag; }
    
    public String getEndpointId() { return endpointId; }
    public void setEndpointId(String endpointId) { this.endpointId = endpointId; }
    
    // Alias for createdAt
    public Instant getCreatedDate() { return createdAt; }
}