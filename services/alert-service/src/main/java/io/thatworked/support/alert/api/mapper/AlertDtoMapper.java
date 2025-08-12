package io.thatworked.support.alert.api.mapper;

import io.thatworked.support.alert.api.dto.response.AlertDTO;
import io.thatworked.support.alert.domain.model.AlertDomain;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between AlertDomain and DTOs.
 */
@Component
public class AlertDtoMapper {
    
    /**
     * Convert domain model to DTO.
     */
    public AlertDTO toDTO(AlertDomain domain) {
        if (domain == null) {
            return null;
        }
        
        return AlertDTO.builder()
            .id(domain.getId())
            .deviceId(domain.getDeviceId())
            .deviceName(domain.getDeviceName())
            .alertType(domain.getAlertType().name())
            .message(domain.getMessage())
            .timestamp(domain.getTimestamp())
            .isResolved(domain.isResolved())
            .resolvedAt(domain.getResolvedAt())
            .isAcknowledged(domain.isAcknowledged())
            .acknowledgedAt(domain.getAcknowledgedAt())
            .acknowledgedBy(domain.getAcknowledgedBy())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .version(domain.getVersion())
            .build();
    }
}