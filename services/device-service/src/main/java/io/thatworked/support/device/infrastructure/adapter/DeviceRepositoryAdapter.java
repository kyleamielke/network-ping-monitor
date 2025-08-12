package io.thatworked.support.device.infrastructure.adapter;

import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.model.DeviceRoleDomain;
import io.thatworked.support.device.domain.model.DeviceStatus;
import io.thatworked.support.device.domain.port.DeviceRepository;
import io.thatworked.support.device.infrastructure.entity.Device;
import io.thatworked.support.device.infrastructure.entity.DeviceRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter that implements the domain DeviceRepository port using JPA.
 */
@Component
@RequiredArgsConstructor
public class DeviceRepositoryAdapter implements DeviceRepository {
    
    private final io.thatworked.support.device.infrastructure.repository.DeviceRepository jpaRepository;
    private final EntityManager entityManager;
    
    @Override
    @Transactional
    public DeviceDomain save(DeviceDomain device) {
        Device entity;
        
        // For updates, we need to fetch the existing entity to preserve the version
        if (device.getId() != null && jpaRepository.existsById(device.getId())) {
            entity = jpaRepository.findById(device.getId()).orElseThrow();
            // Update fields from domain
            updateEntityFromDomain(entity, device);
        } else {
            // For new entities, create from scratch
            entity = toEntity(device);
        }
        
        Device saved = jpaRepository.saveAndFlush(entity);
        // Refresh the entity to get the updated version from the database
        entityManager.refresh(saved);
        return toDomain(saved);
    }
    
    @Override
    public Optional<DeviceDomain> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }
    
    @Override
    public Optional<DeviceDomain> findByIpAddress(String ipAddress) {
        return jpaRepository.findByIpAddress(ipAddress).map(this::toDomain);
    }
    
    @Override
    public Optional<DeviceDomain> findByMacAddress(String macAddress) {
        return jpaRepository.findByMacAddress(macAddress).map(this::toDomain);
    }
    
    @Override
    public boolean existsByIpAddress(String ipAddress) {
        return jpaRepository.existsByIpAddress(ipAddress);
    }
    
    @Override
    public boolean existsByMacAddress(String macAddress) {
        return jpaRepository.existsByMacAddress(macAddress);
    }
    
    @Override
    public List<DeviceDomain> findAll() {
        return jpaRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceDomain> findBySiteId(UUID siteId) {
        return jpaRepository.findBySite(siteId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceDomain> findByDeviceType(String deviceType) {
        return jpaRepository.findByType(deviceType).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public long countBySiteId(UUID siteId) {
        return jpaRepository.countBySiteId(siteId);
    }
    
    // Mapping methods
    private DeviceDomain toDomain(Device entity) {
        // Parse status
        DeviceStatus status = DeviceStatus.ACTIVE;
        if (entity.getStatus() != null) {
            try {
                status = DeviceStatus.valueOf(entity.getStatus());
            } catch (IllegalArgumentException e) {
                // Use default
            }
        }
        
        // No need to parse - just use the strings directly
        String osType = entity.getOsType();
        String deviceType = entity.getType();
        
        // Use full constructor for existing devices
        return new DeviceDomain(
            entity.getId(),
            entity.getName(),
            entity.getIpAddress(),
            entity.getHostname(),
            entity.getMacAddress(),
            entity.getOs(),
            osType,
            entity.getMake(),
            entity.getModel(),
            deviceType,
            entity.getEndpointId(),
            entity.getAssetTag(),
            entity.getDescription(),
            entity.getLocation(),
            status,
            entity.getMetadata(),
            entity.getSite(),
            mapRolesToDomain(entity.getDeviceRoles()),
            entity.getVersion(),
            entity.getCreatedAt() != null ? entity.getCreatedAt() : Instant.now(),
            entity.getUpdatedAt() != null ? entity.getUpdatedAt() : Instant.now()
        );
    }
    
    private void updateEntityFromDomain(Device entity, DeviceDomain domain) {
        // Update all fields except ID and version (which are managed by JPA)
        entity.setName(domain.getName());
        entity.setIpAddress(domain.getIpAddress());
        entity.setHostname(domain.getHostname());
        entity.setMacAddress(domain.getMacAddress());
        entity.setOs(domain.getOs());
        entity.setOsType(domain.getOsType());
        entity.setType(domain.getType());
        entity.setMake(domain.getMake());
        entity.setModel(domain.getModel());
        entity.setEndpointId(domain.getEndpointId());
        entity.setAssetTag(domain.getAssetTag());
        entity.setDescription(domain.getDescription());
        entity.setLocation(domain.getLocation());
        entity.setMetadata(domain.getMetadata());
        entity.setSite(domain.getSiteId());
        entity.setStatus(domain.getStatus().name());
        entity.setDeviceRoles(mapRolesToEntity(domain.getRoles()));
        entity.setUpdatedAt(Instant.now());
    }
    
    private Device toEntity(DeviceDomain domain) {
        return Device.builder()
            .id(domain.getId())
            .version(domain.getVersion())
            .name(domain.getName())
            .ipAddress(domain.getIpAddress())
            .hostname(domain.getHostname())
            .macAddress(domain.getMacAddress())
            .os(domain.getOs())
            .osType(domain.getOsType())
            .type(domain.getType())
            .make(domain.getMake())
            .model(domain.getModel())
            .endpointId(domain.getEndpointId())
            .assetTag(domain.getAssetTag())
            .description(domain.getDescription())
            .location(domain.getLocation())
            .metadata(domain.getMetadata())
            .site(domain.getSiteId())
            .status(domain.getStatus().name())
            .deviceRoles(mapRolesToEntity(domain.getRoles()))
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
    
    private Set<DeviceRoleDomain> mapRolesToDomain(Set<DeviceRole> entityRoles) {
        if (entityRoles == null) {
            return new HashSet<>();
        }
        return entityRoles.stream()
            .map(role -> new DeviceRoleDomain(
                role.getId(),
                role.getRole(),
                "Role for device", // Default description
                Instant.now() // Use current time as placeholder
            ))
            .collect(Collectors.toSet());
    }
    
    private Set<DeviceRole> mapRolesToEntity(Set<DeviceRoleDomain> domainRoles) {
        if (domainRoles == null) {
            return new HashSet<>();
        }
        return domainRoles.stream()
            .map(role -> {
                DeviceRole entityRole = new DeviceRole(role.getName());
                entityRole.setId(role.getId());
                return entityRole;
            })
            .collect(Collectors.toSet());
    }
}