package io.thatworked.support.device.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Pure domain model for Device.
 * No framework dependencies, no annotations except standard Java.
 * This represents the core business entity.
 */
public class DeviceDomain {
    
    private final UUID id;
    private Long version;
    private String name;
    private String ipAddress;
    private String hostname;
    private String macAddress;
    private String os;
    private String osType;  // Changed from OSType enum to String
    private String make;
    private String model;
    private String type;  // Changed from DeviceType enum to String
    private String endpointId;
    private String assetTag;
    private String description;
    private String location;
    private DeviceStatus status;
    private Map<String, String> metadata;
    private UUID siteId;
    private Set<DeviceRoleDomain> roles;
    private final Instant createdAt;
    private Instant updatedAt;

    // Constructor for new devices
    public DeviceDomain(String name, String ipAddress) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.ipAddress = ipAddress;
        this.status = DeviceStatus.ACTIVE;
        this.metadata = new HashMap<>();
        this.roles = new HashSet<>();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    // Constructor for existing devices (from persistence)
    public DeviceDomain(UUID id, String name, String ipAddress, String hostname, String macAddress, 
                       String os, String osType, String make, String model, 
                       String type, String endpointId, String assetTag, 
                       String description, String location, DeviceStatus status, 
                       Map<String, String> metadata, UUID siteId, 
                       Set<DeviceRoleDomain> roles, Long version, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.macAddress = macAddress;
        this.os = os;
        this.osType = osType;
        this.make = make;
        this.model = model;
        this.type = type;
        this.endpointId = endpointId;
        this.assetTag = assetTag;
        this.description = description;
        this.location = location;
        this.status = status;
        this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
        this.siteId = siteId;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Business methods
    public void updateDetails(String name, String ipAddress, String hostname, String macAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.hostname = hostname;
        this.macAddress = macAddress;
        this.updatedAt = Instant.now();
    }

    public void updateSystemInfo(String os, String osType, String make, String model) {
        this.os = os;
        this.osType = osType;
        this.make = make;
        this.model = model;
        this.updatedAt = Instant.now();
    }

    public void updateAdditionalInfo(String type, String make, String model) {
        if (type != null) {
            this.type = type;
        }
        if (make != null) {
            this.make = make;
        }
        if (model != null) {
            this.model = model;
        }
        this.updatedAt = Instant.now();
    }
    
    public void setDescriptionAndLocation(String description, String location) {
        if (description != null) {
            this.description = description;
        }
        if (location != null) {
            this.location = location;
        }
        this.updatedAt = Instant.now();
    }
    
    public void updateIdentifiers(String endpointId, String assetTag) {
        if (endpointId != null) {
            this.endpointId = endpointId;
        }
        if (assetTag != null) {
            this.assetTag = assetTag;
        }
        this.updatedAt = Instant.now();
    }
    
    public void updateMetadata(Map<String, String> newMetadata) {
        if (newMetadata != null) {
            this.metadata.clear();
            this.metadata.putAll(newMetadata);
            this.updatedAt = Instant.now();
        }
    }

    public void assignToSite(UUID siteId) {
        this.siteId = siteId;
        this.updatedAt = Instant.now();
    }

    public void removeFromSite() {
        this.siteId = null;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == DeviceStatus.INACTIVE) {
            this.status = DeviceStatus.ACTIVE;
            this.updatedAt = Instant.now();
        }
    }

    public void deactivate() {
        if (this.status == DeviceStatus.ACTIVE) {
            this.status = DeviceStatus.INACTIVE;
            this.updatedAt = Instant.now();
        }
    }

    public void addRole(DeviceRoleDomain role) {
        if (roles.add(role)) {
            this.updatedAt = Instant.now();
        }
    }

    public void removeRole(DeviceRoleDomain role) {
        if (roles.remove(role)) {
            this.updatedAt = Instant.now();
        }
    }

    public void addMetadata(String key, String value) {
        metadata.put(key, value);
        this.updatedAt = Instant.now();
    }

    public void removeMetadata(String key) {
        if (metadata.remove(key) != null) {
            this.updatedAt = Instant.now();
        }
    }

    // Business validation methods
    public boolean isActive() {
        return status == DeviceStatus.ACTIVE;
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
            .anyMatch(role -> role.getName().equals(roleName));
    }

    public boolean isAssignedToSite() {
        return siteId != null;
    }

    // Getters only (immutability for most fields)
    public UUID getId() {
        return id;
    }
    
    public Long getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getHostname() {
        return hostname;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getOs() {
        return os;
    }

    public String getOsType() {
        return osType;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public Map<String, String> getMetadata() {
        return new HashMap<>(metadata);
    }

    public UUID getSiteId() {
        return siteId;
    }

    public Set<DeviceRoleDomain> getRoles() {
        return new HashSet<>(roles);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceDomain)) return false;
        DeviceDomain that = (DeviceDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DeviceDomain{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", status=" + status +
                '}';
    }
    
    /**
     * Factory method to create DeviceDomain from entity data.
     * Used by query services to convert from persistence layer.
     */
    public static DeviceDomain fromEntity(UUID id, String name, String ipAddress, String hostname, String macAddress,
                                         String os, String osType, String type,
                                         String make, String model, String endpointId, String assetTag,
                                         String description, String location, Map<String, String> metadata,
                                         UUID siteId, Long version, Instant createdAt, Instant updatedAt) {
        // Use the full constructor for existing devices
        return new DeviceDomain(
            id, name, ipAddress, hostname, macAddress,
            os, osType, make, model,
            type, endpointId, assetTag,
            description, location, DeviceStatus.ACTIVE,
            metadata != null ? metadata : new HashMap<>(),
            siteId,
            new HashSet<>(), // roles will be loaded separately if needed
            version,
            createdAt != null ? createdAt : Instant.now(),
            updatedAt != null ? updatedAt : Instant.now()
        );
    }
}