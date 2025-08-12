package io.thatworked.support.notification.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * API response for notification result.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResultDto {
    private UUID notificationRequestId;
    private boolean successful;
    private String message;
    private String errorDetails;
    private Instant sentAt;
    private String channelSpecificId;
}