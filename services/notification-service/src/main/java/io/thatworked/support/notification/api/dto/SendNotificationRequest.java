package io.thatworked.support.notification.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * API request for sending a notification.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendNotificationRequest {
    
    @NotNull(message = "Notification type is required")
    private NotificationTypeDto type;
    
    @NotNull(message = "Channel is required")
    private NotificationChannelDto channel;
    
    @NotBlank(message = "Recipient is required")
    private String recipient;
    
    private String subject;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    private Map<String, Object> metadata;
    
    private UUID sourceEventId;
}