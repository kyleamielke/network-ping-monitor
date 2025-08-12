package io.thatworked.support.device.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Pure domain model for DeviceRole.
 * No framework dependencies, no annotations.
 * Represents a role that can be assigned to devices.
 */
public class DeviceRoleDomain {
    
    private final UUID id;
    private final String name;
    private final String description;
    private final Instant createdAt;

    // Constructor for creating new roles
    public DeviceRoleDomain(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.createdAt = Instant.now();
    }

    // Constructor for existing roles (from persistence)
    public DeviceRoleDomain(UUID id, String name, String description, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Business validation
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }

    // Getters only (immutable)
    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceRoleDomain)) return false;
        DeviceRoleDomain that = (DeviceRoleDomain) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "DeviceRoleDomain{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}