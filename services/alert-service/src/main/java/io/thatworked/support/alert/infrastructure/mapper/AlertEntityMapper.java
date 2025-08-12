package io.thatworked.support.alert.infrastructure.mapper;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.infrastructure.entity.Alert;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between Alert entity and AlertDomain.
 */
@Component
public class AlertEntityMapper {
    
    /**
     * Convert domain model to entity.
     */
    public Alert toEntity(AlertDomain domain) {
        Alert.AlertBuilder builder = Alert.builder()
            .id(domain.getId())
            .deviceId(domain.getDeviceId())
            .deviceName(domain.getDeviceName())
            .alertType(domain.getAlertType())
            .message(domain.getMessage())
            .timestamp(domain.getTimestamp())
            .isResolved(domain.isResolved())
            .resolvedAt(domain.getResolvedAt())
            .isAcknowledged(domain.isAcknowledged())
            .acknowledgedAt(domain.getAcknowledgedAt())
            .acknowledgedBy(domain.getAcknowledgedBy())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .ipAddress(domain.getIpAddress())
            .consecutiveFailures(domain.getConsecutiveFailures())
            .failureReason(domain.getFailureReason());
        
        // Only set version if it's not null (for updates)
        if (domain.getVersion() != null) {
            builder.version(domain.getVersion());
        }
        
        return builder.build();
    }
    
    /**
     * Convert entity to domain model.
     */
    public AlertDomain toDomain(Alert entity) {
        return new AlertDomain(
            entity.getId(),
            entity.getDeviceId(),
            entity.getDeviceName(),
            entity.getAlertType(),
            entity.getMessage(),
            entity.getTimestamp(),
            entity.isResolved(),
            entity.getResolvedAt(),
            entity.isAcknowledged(),
            entity.getAcknowledgedAt(),
            entity.getAcknowledgedBy(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getIpAddress(),
            entity.getConsecutiveFailures(),
            entity.getFailureReason(),
            entity.getVersion()
        );
    }
}