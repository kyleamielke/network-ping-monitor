package io.thatworked.support.notification.domain.service;

import io.thatworked.support.notification.domain.exception.InvalidNotificationRequestException;
import io.thatworked.support.notification.domain.exception.NotificationSendException;
import io.thatworked.support.notification.domain.exception.UnsupportedChannelException;
import io.thatworked.support.notification.domain.model.NotificationChannel;
import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.model.NotificationResult;
import io.thatworked.support.notification.domain.model.NotificationType;
import io.thatworked.support.notification.domain.port.DomainLogger;
import io.thatworked.support.notification.domain.port.EventPublisher;
import io.thatworked.support.notification.domain.port.NotificationRepository;
import io.thatworked.support.notification.domain.port.NotificationSender;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain service for notification business logic.
 * Orchestrates notification sending and persistence.
 */
public class NotificationDomainService {
    
    private final NotificationRepository repository;
    private final NotificationSender sender;
    private final EventPublisher eventPublisher;
    private final DomainLogger logger;
    
    public NotificationDomainService(NotificationRepository repository,
                                   NotificationSender sender,
                                   EventPublisher eventPublisher,
                                   DomainLogger logger) {
        this.repository = repository;
        this.sender = sender;
        this.eventPublisher = eventPublisher;
        this.logger = logger;
    }
    
    /**
     * Send a notification based on the request.
     */
    public NotificationResult sendNotification(NotificationRequest request) {
        validateRequest(request);
        
        // Log business event
        Map<String, Object> context = new HashMap<>();
        context.put("notificationId", request.getId());
        context.put("type", request.getType());
        context.put("channel", request.getChannel());
        context.put("recipient", request.getRecipient());
        logger.logBusinessEvent("Notification requested", context);
        
        // Save the request
        NotificationRequest savedRequest = repository.saveRequest(request);
        eventPublisher.publishNotificationRequested(savedRequest);
        
        // Check if channel is supported
        if (!request.getChannel().isEnabled()) {
            String error = "Channel not enabled: " + request.getChannel();
            logger.logBusinessWarning(error, context);
            throw new UnsupportedChannelException(request.getChannel().name());
        }
        
        if (!sender.supportsChannel(request.getChannel().name())) {
            String error = "Channel not supported by sender: " + request.getChannel();
            logger.logBusinessWarning(error, context);
            throw new UnsupportedChannelException(request.getChannel().name());
        }
        
        // Send the notification
        NotificationResult result;
        try {
            result = sender.send(savedRequest);
            
            // Save the result
            result = repository.saveResult(result);
            
            // Log success
            Map<String, Object> successContext = new HashMap<>(context);
            successContext.put("channelSpecificId", result.getChannelSpecificId());
            logger.logBusinessEvent("Notification sent successfully", successContext);
            
            // Publish success event
            eventPublisher.publishNotificationSent(savedRequest, result);
            
        } catch (NotificationSendException e) {
            // Re-throw domain exceptions
            throw e;
        } catch (UnsupportedChannelException e) {
            // Re-throw domain exceptions
            throw e;
        } catch (Exception e) {
            // Create failure result for unexpected errors
            result = NotificationResult.failure(
                savedRequest.getId(),
                "Failed to send notification",
                e.getMessage()
            );
            
            // Save the failure result
            result = repository.saveResult(result);
            
            // Log failure
            Map<String, Object> failureContext = new HashMap<>(context);
            failureContext.put("error", e.getMessage());
            logger.logBusinessWarning("Notification send failed", failureContext);
            
            // Publish failure event
            eventPublisher.publishNotificationFailed(savedRequest, result);
            
            throw new NotificationSendException(
                "Failed to send notification", 
                request.getChannel().name(), 
                request.getRecipient(),
                e
            );
        }
        
        return result;
    }
    
    /**
     * Get notification history for a time range.
     */
    public List<NotificationRequest> getNotificationHistory(Instant startTime, Instant endTime) {
        Map<String, Object> context = new HashMap<>();
        context.put("startTime", startTime);
        context.put("endTime", endTime);
        logger.logBusinessEvent("Retrieving notification history", context);
        
        return repository.findRequestsByTimeRange(startTime, endTime);
    }
    
    /**
     * Get failed notifications for retry processing.
     */
    public List<NotificationRequest> getFailedNotifications(Instant startTime, Instant endTime) {
        Map<String, Object> context = new HashMap<>();
        context.put("startTime", startTime);
        context.put("endTime", endTime);
        logger.logBusinessEvent("Retrieving failed notifications", context);
        
        return repository.findFailedRequestsByTimeRange(startTime, endTime);
    }
    
    /**
     * Get notifications related to a specific event.
     */
    public List<NotificationRequest> getNotificationsBySourceEvent(UUID sourceEventId) {
        Map<String, Object> context = new HashMap<>();
        context.put("sourceEventId", sourceEventId);
        logger.logBusinessEvent("Retrieving notifications by source event", context);
        
        return repository.findRequestsBySourceEventId(sourceEventId);
    }
    
    private void validateRequest(NotificationRequest request) {
        if (request == null) {
            throw new InvalidNotificationRequestException("Notification request cannot be null");
        }
        
        if (request.getRecipient() == null || request.getRecipient().trim().isEmpty()) {
            throw new InvalidNotificationRequestException("Recipient is required");
        }
        
        if (request.getType() == null) {
            throw new InvalidNotificationRequestException("Notification type is required");
        }
        
        if (request.getChannel() == null) {
            throw new InvalidNotificationRequestException("Notification channel is required");
        }
        
        if (request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new InvalidNotificationRequestException("Message is required");
        }
        
        // Channel-specific validation
        if (request.getChannel() == NotificationChannel.EMAIL) {
            if (request.getSubject() == null || request.getSubject().trim().isEmpty()) {
                throw new InvalidNotificationRequestException("Subject is required for email notifications");
            }
            
            // Basic email validation
            if (!request.getRecipient().contains("@")) {
                throw new InvalidNotificationRequestException("Invalid email recipient: " + request.getRecipient());
            }
        }
    }
}