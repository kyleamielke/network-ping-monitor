package io.thatworked.support.notification.api.controller;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.api.dto.*;
import io.thatworked.support.notification.api.mapper.NotificationApiMapper;
import io.thatworked.support.notification.application.dto.*;
import io.thatworked.support.notification.application.usecase.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST controller for notification operations.
 */
@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    
    private final StructuredLogger logger;
    private final SendNotificationUseCase sendNotificationUseCase;
    private final GetNotificationHistoryUseCase getNotificationHistoryUseCase;
    private final GetNotificationsBySourceEventUseCase getNotificationsBySourceEventUseCase;
    private final NotificationApiMapper mapper;
    
    @Value("${notification-service.service.test.message:This is a test notification from the NetworkPing Monitor system.}")
    private String testMessage;
    
    @Value("${notification-service.service.test.subject:Test Notification}")
    private String testSubject;
    
    public NotificationController(StructuredLoggerFactory loggerFactory,
                                SendNotificationUseCase sendNotificationUseCase,
                                GetNotificationHistoryUseCase getNotificationHistoryUseCase,
                                GetNotificationsBySourceEventUseCase getNotificationsBySourceEventUseCase,
                                NotificationApiMapper mapper) {
        this.logger = loggerFactory.getLogger(NotificationController.class);
        this.sendNotificationUseCase = sendNotificationUseCase;
        this.getNotificationHistoryUseCase = getNotificationHistoryUseCase;
        this.getNotificationsBySourceEventUseCase = getNotificationsBySourceEventUseCase;
        this.mapper = mapper;
    }
    
    /**
     * Send a notification.
     * This endpoint is primarily for testing purposes.
     */
    @PostMapping
    public ResponseEntity<NotificationResultDto> sendNotification(@Valid @RequestBody SendNotificationRequest request) {
        logger.with("operation", "sendNotification")
                .with("type", request.getType())
                .with("channel", request.getChannel())
                .with("recipient", request.getRecipient())
                .info("Processing notification request");
        
        SendNotificationCommand command = mapper.toCommand(request);
        NotificationResponse response;
        try {
            response = sendNotificationUseCase.execute(command);
        } catch (Exception e) {
            logger.with("operation", "sendNotification")
                    .with("error", e.getMessage())
                    .with("errorType", e.getClass().getSimpleName())
                    .error("Failed to send notification", e);
            throw e;
        }
        NotificationResultDto result = mapper.toResultDto(response);
        
        if (response.isSuccessful()) {
            logger.with("operation", "sendNotification")
                    .with("notificationId", response.getNotificationRequestId())
                    .with("channel", request.getChannel())
                    .info("Notification sent successfully");
            return ResponseEntity.ok(result);
        } else {
            logger.with("operation", "sendNotification")
                    .with("notificationId", response.getNotificationRequestId())
                    .with("error", response.getErrorDetails())
                    .warn("Notification failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * Get notification history within a time range.
     */
    @GetMapping("/history")
    public ResponseEntity<List<NotificationHistoryDto>> getNotificationHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @RequestParam(defaultValue = "false") boolean failedOnly) {
        
        logger.with("operation", "getNotificationHistory")
                .with("startTime", startTime)
                .with("endTime", endTime)
                .with("failedOnly", failedOnly)
                .info("Retrieving notification history");
        
        NotificationHistoryQuery query = new NotificationHistoryQuery(startTime, endTime, failedOnly);
        List<NotificationHistoryResponse> responses = getNotificationHistoryUseCase.execute(query);
        
        List<NotificationHistoryDto> history = responses.stream()
            .map(mapper::toHistoryDto)
            .collect(Collectors.toList());
        
        logger.with("operation", "getNotificationHistory")
                .with("count", history.size())
                .info("Notification history retrieved");
        
        return ResponseEntity.ok(history);
    }
    
    /**
     * Get notifications for a specific source event.
     */
    @GetMapping("/by-event/{sourceEventId}")
    public ResponseEntity<List<NotificationHistoryDto>> getNotificationsBySourceEvent(@PathVariable UUID sourceEventId) {
        logger.with("operation", "getNotificationsBySourceEvent")
                .with("sourceEventId", sourceEventId)
                .info("Retrieving notifications by source event");
        
        List<NotificationHistoryResponse> responses = getNotificationsBySourceEventUseCase.execute(sourceEventId);
        
        List<NotificationHistoryDto> notifications = responses.stream()
            .map(mapper::toHistoryDto)
            .collect(Collectors.toList());
        
        logger.with("operation", "getNotificationsBySourceEvent")
                .with("sourceEventId", sourceEventId)
                .with("count", notifications.size())
                .info("Notifications by event retrieved");
        
        return ResponseEntity.ok(notifications);
    }
    
    /**
     * Send a test notification.
     */
    @PostMapping("/test")
    public ResponseEntity<NotificationResultDto> sendTestNotification(
            @RequestParam NotificationChannelDto channel,
            @RequestParam String recipient) {
        
        logger.with("operation", "sendTestNotification")
                .with("channel", channel)
                .with("recipient", recipient)
                .info("Sending test notification");
        
        SendNotificationRequest request = new SendNotificationRequest();
        request.setType(NotificationTypeDto.TEST);
        request.setChannel(channel);
        request.setRecipient(recipient);
        request.setSubject(testSubject);
        request.setMessage(testMessage);
        request.setSourceEventId(UUID.randomUUID());
        
        return sendNotification(request);
    }
}