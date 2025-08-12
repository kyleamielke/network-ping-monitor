package io.thatworked.support.notification.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * API DTO for notification history.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationHistoryDto {
    private UUID id;
    private NotificationTypeDto type;
    private NotificationChannelDto channel;
    private String recipient;
    private String subject;
    private String message;
    private Map<String, Object> metadata;
    private Instant requestedAt;
    private UUID sourceEventId;
}