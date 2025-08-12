package io.thatworked.support.ping.api.mapper;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.api.dto.PingTargetDTO;
import org.springframework.stereotype.Component;

/**
 * Mapper between PingTargetDomain and PingTargetDTO.
 */
@Component
public class PingTargetDomainMapper {
    
    public PingTargetDTO toDTO(PingTargetDomain domain) {
        if (domain == null) {
            return null;
        }
        
        return PingTargetDTO.builder()
            .deviceId(domain.getDeviceId())
            .ipAddress(domain.getIpAddress())
            .hostname(domain.getHostname())
            .monitored(domain.isMonitored())
            .pingIntervalSeconds(domain.getPingIntervalSeconds())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }
}