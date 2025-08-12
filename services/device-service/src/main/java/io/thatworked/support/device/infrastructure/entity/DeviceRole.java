package io.thatworked.support.device.infrastructure.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "device_role",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_device_role", columnNames = "role")
    },
    indexes = {
        @Index(name = "idx_device_role_name", columnList = "role")
    }
)
public class DeviceRole {
    @Id
    @Column(nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "role", nullable = false, unique = true)
    private String role;

    @ManyToMany(mappedBy = "deviceRoles")
    private Set<Device> devices = new HashSet<>();

    // Simple constructor
    public DeviceRole(String role) {
        this.role = role;
        this.id = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceRole)) return false;
        DeviceRole that = (DeviceRole) o;
        return role != null && role.equals(that.getRole());
    }

    @Override
    public int hashCode() {
        return role != null ? role.hashCode() : 0;
    }
}