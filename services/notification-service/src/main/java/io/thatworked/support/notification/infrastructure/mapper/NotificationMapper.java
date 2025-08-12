package io.thatworked.support.notification.infrastructure.mapper;

import io.thatworked.support.notification.domain.model.*;
import io.thatworked.support.notification.infrastructure.entity.NotificationRequestEntity;
import io.thatworked.support.notification.infrastructure.entity.NotificationResultEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper between domain models and infrastructure entities.
 */
@Component
public class NotificationMapper {
    
    public NotificationRequestEntity toEntity(NotificationRequest domain) {
        NotificationRequestEntity entity = new NotificationRequestEntity();
        // Don't set ID for new entities - let JPA generate it
        entity.setNotificationType(domain.getType().name());
        entity.setChannel(domain.getChannel().name());
        entity.setRecipient(domain.getRecipient());
        entity.setSubject(domain.getSubject());
        entity.setMessage(domain.getMessage());
        
        // Convert metadata to string map
        Map<String, String> stringMetadata = domain.getMetadata().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue() != null ? e.getValue().toString() : ""
            ));
        entity.setMetadata(stringMetadata);
        
        // Don't set requestedAt - it has @CreationTimestamp
        entity.setSourceEventId(domain.getSourceEventId());
        
        return entity;
    }
    
    public NotificationRequest toDomain(NotificationRequestEntity entity) {
        // Convert string metadata back to object map
        Map<String, Object> objectMetadata = new HashMap<>(entity.getMetadata());
        
        return new NotificationRequest(
            entity.getId(),
            NotificationType.valueOf(entity.getNotificationType()),
            NotificationChannel.valueOf(entity.getChannel()),
            entity.getRecipient(),
            entity.getSubject(),
            entity.getMessage(),
            objectMetadata,
            entity.getRequestedAt(),
            entity.getSourceEventId()
        );
    }
    
    public NotificationResultEntity toEntity(NotificationResult domain) {
        NotificationResultEntity entity = new NotificationResultEntity();
        entity.setNotificationRequestId(domain.getNotificationRequestId());
        entity.setSuccessful(domain.isSuccessful());
        entity.setMessage(domain.getMessage());
        entity.setErrorDetails(domain.getErrorDetails());
        entity.setSentAt(domain.getSentAt());
        entity.setChannelSpecificId(domain.getChannelSpecificId());
        
        return entity;
    }
    
    public NotificationResult toDomain(NotificationResultEntity entity) {
        if (entity.isSuccessful()) {
            return NotificationResult.success(
                entity.getNotificationRequestId(),
                entity.getMessage(),
                entity.getChannelSpecificId()
            );
        } else {
            return NotificationResult.failure(
                entity.getNotificationRequestId(),
                entity.getMessage(),
                entity.getErrorDetails()
            );
        }
    }
}