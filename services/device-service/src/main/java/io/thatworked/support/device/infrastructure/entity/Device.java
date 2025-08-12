package io.thatworked.support.device.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "device",
    indexes = {
        @Index(name = "idx_device_type", columnList = "type"),
        @Index(name = "idx_device_os_type", columnList = "os_type"),
        @Index(name = "idx_endpoint_id", columnList = "endpoint_id"),
        @Index(name = "idx_asset_tag", columnList = "asset_tag"),
        @Index(name = "idx_mac_address", columnList = "mac_address"),
        @Index(name = "idx_ip_address", columnList = "ip_address"),
        @Index(name = "idx_hostname", columnList = "hostname"),
        @Index(name = "idx_site", columnList = "site")
    }
)
public class Device {
    @Id
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "hostname")
    private String hostname;

    @Column(name = "mac_address")
    private String macAddress;

    @Column(name = "os")
    private String os;

    @Column(name = "os_type")
    private String osType;

    @Column(name = "make")
    private String make;

    @Column(name = "model")
    private String model;

    @Column(name = "type")
    private String type;

    @Column(name = "endpoint_id")
    private String endpointId;

    @Column(name = "asset_tag")
    private String assetTag;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "location")
    private String location;
    
    @Column(name = "status")
    private String status;
    
    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "device_metadata", joinColumns = @JoinColumn(name = "device_id"))
    @MapKeyColumn(name = "metadata_key")
    @Column(name = "metadata_value")
    private Map<String, String> metadata = new HashMap<>();

    // Optional reference to a site by UUID without direct coupling
    @Column(name = "site", columnDefinition = "uuid")
    private UUID site;
    
    // Optimistic locking
    @Version
    @Column(name = "version")
    private Long version;
    
    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Transient
    private String rolesString;

    // Temporarily disabled device roles to fix UUID column issues
    @Builder.Default
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
        name = "device_role_mapping",
        joinColumns = @JoinColumn(name = "device_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<DeviceRole> deviceRoles = new HashSet<>();

    // Helper methods for managing relationships
    public void addRole(DeviceRole role) {
        deviceRoles.add(role);
        role.getDevices().add(this);
    }

    public void removeRole(DeviceRole role) {
        deviceRoles.remove(role);
        role.getDevices().remove(this);
    }

    @PostLoad
    @PrePersist
    public void ensureUuid() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.deviceRoles == null) {
            this.deviceRoles = new HashSet<>();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device)) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}