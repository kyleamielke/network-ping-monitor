package io.thatworked.support.device.infrastructure.repository;

import io.thatworked.support.device.infrastructure.entity.DeviceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface DeviceRoleRepository extends JpaRepository<DeviceRole, UUID> {
    Optional<DeviceRole> findByRole(String role);
    boolean existsByRole(String role);

    @Query("SELECT dr FROM DeviceRole dr LEFT JOIN FETCH dr.devices WHERE dr.role IN :roles")
    Set<DeviceRole> findByRolesWithDevices(List<String> roles);

    @Query("SELECT DISTINCT dr.role FROM DeviceRole dr")
    List<String> findAllRoles();
}