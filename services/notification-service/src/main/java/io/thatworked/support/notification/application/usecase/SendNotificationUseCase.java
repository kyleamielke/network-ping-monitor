package io.thatworked.support.notification.application.usecase;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.application.dto.NotificationResponse;
import io.thatworked.support.notification.application.dto.SendNotificationCommand;
import io.thatworked.support.notification.domain.model.NotificationChannel;
import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.model.NotificationResult;
import io.thatworked.support.notification.domain.model.NotificationType;
import io.thatworked.support.notification.domain.service.NotificationDomainService;
import org.springframework.stereotype.Service;

/**
 * Use case for sending notifications.
 * Translates between application DTOs and domain objects.
 */
@Service
public class SendNotificationUseCase {
    
    private final StructuredLogger logger;
    private final NotificationDomainService domainService;
    
    public SendNotificationUseCase(StructuredLoggerFactory loggerFactory,
                                   NotificationDomainService domainService) {
        this.logger = loggerFactory.getLogger(SendNotificationUseCase.class);
        this.domainService = domainService;
    }
    
    public NotificationResponse execute(SendNotificationCommand command) {
        logger.with("operation", "sendNotification")
                .with("notificationType", command.getNotificationType())
                .with("channel", command.getChannel())
                .with("recipient", command.getRecipient())
                .debug("Executing send notification use case");
        
        try {
            // Convert command to domain object
            NotificationType type = NotificationType.valueOf(command.getNotificationType());
            NotificationChannel channel = NotificationChannel.valueOf(command.getChannel());
            
            NotificationRequest request = new NotificationRequest(
                type,
                channel,
                command.getRecipient(),
                command.getSubject(),
                command.getMessage(),
                command.getMetadata(),
                command.getSourceEventId()
            );
            
            // Execute domain logic
            NotificationResult result = domainService.sendNotification(request);
            
            // Convert to response DTO
            NotificationResponse response = new NotificationResponse(
                result.getNotificationRequestId(),
                result.isSuccessful(),
                result.getMessage(),
                result.getErrorDetails(),
                result.getSentAt(),
                result.getChannelSpecificId()
            );
            
            logger.with("operation", "sendNotification")
                    .with("notificationId", response.getNotificationRequestId())
                    .with("successful", response.isSuccessful())
                    .info("Send notification use case completed");
            
            return response;
        } catch (Exception e) {
            logger.with("operation", "sendNotification")
                    .with("error", e.getMessage())
                    .with("errorType", e.getClass().getSimpleName())
                    .error("Failed to execute send notification use case", e);
            throw e;
        }
    }
}