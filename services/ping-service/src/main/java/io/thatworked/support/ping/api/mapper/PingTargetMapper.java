package io.thatworked.support.ping.api.mapper;

import io.thatworked.support.ping.domain.PingTarget;
import io.thatworked.support.ping.api.dto.PingTargetDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PingTargetMapper {

    PingTargetDTO toDTO(PingTarget entity);

    @Mapping(target = "isMonitored", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PingTarget toEntity(PingTargetDTO dto);
}