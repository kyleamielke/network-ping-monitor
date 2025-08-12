package io.thatworked.support.notification.api.mapper;

import io.thatworked.support.notification.api.dto.*;
import io.thatworked.support.notification.application.dto.*;
import org.springframework.stereotype.Component;

/**
 * Mapper between API DTOs and application DTOs.
 */
@Component
public class NotificationApiMapper {
    
    public SendNotificationCommand toCommand(SendNotificationRequest request) {
        return new SendNotificationCommand(
            request.getType().name(),
            request.getChannel().name(),
            request.getRecipient(),
            request.getSubject(),
            request.getMessage(),
            request.getMetadata(),
            request.getSourceEventId()
        );
    }
    
    public NotificationResultDto toResultDto(NotificationResponse response) {
        return NotificationResultDto.builder()
            .notificationRequestId(response.getNotificationRequestId())
            .successful(response.isSuccessful())
            .message(response.getMessage())
            .errorDetails(response.getErrorDetails())
            .sentAt(response.getSentAt())
            .channelSpecificId(response.getChannelSpecificId())
            .build();
    }
    
    public NotificationHistoryDto toHistoryDto(NotificationHistoryResponse response) {
        return NotificationHistoryDto.builder()
            .id(response.getId())
            .type(NotificationTypeDto.valueOf(response.getNotificationType()))
            .channel(NotificationChannelDto.valueOf(response.getChannel()))
            .recipient(response.getRecipient())
            .subject(response.getSubject())
            .message(response.getMessage())
            .metadata(response.getMetadata())
            .requestedAt(response.getRequestedAt())
            .sourceEventId(response.getSourceEventId())
            .build();
    }
}