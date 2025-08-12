package io.thatworked.support.device.api.mapper;

import io.thatworked.support.device.infrastructure.entity.Device;
import io.thatworked.support.device.infrastructure.entity.DeviceRole;
import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.api.dto.request.DeviceCreateRequest;
import io.thatworked.support.device.api.dto.request.DeviceUpdateRequest;
import io.thatworked.support.device.api.dto.response.DeviceDTO;
import io.thatworked.support.device.api.dto.response.RoleDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        imports = {UUID.class},
        uses = {})
public interface DeviceMapper {

    @Mapping(target = "deviceRoles", ignore = true)
    Device toEntity(DeviceDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deviceRoles", ignore = true)
    @Mapping(target = "site", ignore = true)
    @Mapping(target = "rolesString", ignore = true)
    Device toEntity(DeviceCreateRequest request);

    @Mapping(target = "roles", source = "deviceRoles")
    DeviceDTO toDTO(Device device);

    List<DeviceDTO> toDTOList(List<Device> devices);

    Set<DeviceDTO> toDTOSet(Set<Device> devices);

    @Mapping(target = "role", source = "role")
    RoleDTO roleToRoleDTO(DeviceRole role);

    Set<RoleDTO> roleSetToRoleDTOSet(Set<DeviceRole> roles);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "deviceRoles", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntity(DeviceDTO dto, @MappingTarget Device device);

    // Add this conversion method for Object to UUID
    default UUID map(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID) {
            return (UUID) value;
        }
        if (value instanceof String) {
            try {
                return UUID.fromString((String) value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
    
    // Entity to Domain conversion for query services
    default DeviceDomain toDomain(Device entity) {
        if (entity == null) {
            return null;
        }
        return DeviceDomain.fromEntity(
                entity.getId(),
                entity.getName(),
                entity.getIpAddress(),
                entity.getHostname(),
                entity.getMacAddress(),
                entity.getOs(),
                entity.getOsType(),
                entity.getType(),
                entity.getMake(),
                entity.getModel(),
                entity.getEndpointId(),
                entity.getAssetTag(),
                entity.getDescription(),
                entity.getLocation(),
                entity.getMetadata(),
                entity.getSite(), // site is already a UUID
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    
    // Domain to DTO conversion - proper clean architecture approach
    default DeviceDTO toDTO(DeviceDomain domain) {
        if (domain == null) {
            return null;
        }
        return DeviceDTO.builder()
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
                .roles(domain.getRoles() != null ? 
                    domain.getRoles().stream()
                        .map(role -> RoleDTO.builder()
                            .id(role.getId())
                            .role(role.getName())
                            .build())
                        .collect(Collectors.toSet()) : new HashSet<>())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    // DateTime conversion methods
    default OffsetDateTime map(LocalDateTime value) {
        return value != null ? value.atOffset(ZoneOffset.UTC) : null;
    }
    
    default LocalDateTime map(OffsetDateTime value) {
        return value != null ? value.toLocalDateTime() : null;
    }
    
    // No longer needed as DTOs now use Instant directly
}