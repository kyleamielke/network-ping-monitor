package io.thatworked.support.device.api.mapper;

import io.thatworked.support.device.infrastructure.entity.DeviceRole;
import io.thatworked.support.device.api.dto.response.DeviceRoleDTO;
import org.mapstruct.*;

import java.util.Set;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceRoleMapper {

    @Mapping(target = "devices", ignore = true)
    DeviceRole toEntity(DeviceRoleDTO dto);

    DeviceRoleDTO toDTO(DeviceRole entity);

    Set<DeviceRoleDTO> toDTOSet(Set<DeviceRole> entities);

    @IterableMapping(elementTargetType = DeviceRole.class)
    @Mapping(target = "devices", ignore = true)
    Set<DeviceRole> toEntitySet(Set<DeviceRoleDTO> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "devices", ignore = true)
    void updateEntity(DeviceRoleDTO dto, @MappingTarget DeviceRole entity);
}