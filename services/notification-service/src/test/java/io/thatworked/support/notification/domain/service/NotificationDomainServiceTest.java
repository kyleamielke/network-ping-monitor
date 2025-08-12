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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("NotificationDomainService Tests")
class NotificationDomainServiceTest {
    
    @Mock
    private NotificationRepository repository;
    
    @Mock
    private NotificationSender sender;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @Mock
    private DomainLogger logger;
    
    private NotificationDomainService service;
    
    @BeforeEach
    void setUp() {
        service = new NotificationDomainService(repository, sender, eventPublisher, logger);
    }
    
    @Test
    @DisplayName("Should send email notification successfully")
    void testSendEmailNotificationSuccess() {
        // Given
        UUID alertId = UUID.randomUUID();
        String recipient = "admin@example.com";
        String subject = "Alert: Device Down";
        String message = "Device test-device is down";
        
        NotificationRequest request = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            NotificationChannel.EMAIL,
            recipient,
            subject,
            message,
            null,
            alertId
        );
        
        NotificationResult successResult = NotificationResult.success(
            request.getId(),
            "Email sent successfully",
            "msg-id-123"
        );
        
        when(repository.saveRequest(any())).thenReturn(request);
        when(sender.supportsChannel("EMAIL")).thenReturn(true);
        when(sender.send(any())).thenReturn(successResult);
        when(repository.saveResult(any())).thenReturn(successResult);
        
        // When
        NotificationResult result = service.sendNotification(request);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccessful()).isTrue();
        assertThat(result.getChannelSpecificId()).isEqualTo("msg-id-123");
        verify(repository).saveRequest(request);
        verify(sender).send(request);
        verify(repository).saveResult(successResult);
        verify(eventPublisher).publishNotificationRequested(request);
        verify(eventPublisher).publishNotificationSent(request, successResult);
        verify(logger).logBusinessEvent(eq("Notification requested"), any(Map.class));
        verify(logger).logBusinessEvent(eq("Notification sent successfully"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should handle notification send failure")
    void testSendNotificationFailure() {
        // Given
        NotificationRequest request = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            NotificationChannel.EMAIL,
            "admin@example.com",
            "Alert",
            "Device down",
            null,
            UUID.randomUUID()
        );
        
        NotificationResult failureResult = NotificationResult.failure(
            request.getId(),
            "Failed to send notification",
            "SMTP error"
        );
        
        when(repository.saveRequest(any())).thenReturn(request);
        when(sender.supportsChannel("EMAIL")).thenReturn(true);
        when(sender.send(any())).thenThrow(new RuntimeException("SMTP error"));
        when(repository.saveResult(any())).thenReturn(failureResult);
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(request))
            .isInstanceOf(NotificationSendException.class)
            .hasMessageContaining("Failed to send notification");
        
        verify(eventPublisher).publishNotificationRequested(request);
        verify(eventPublisher).publishNotificationFailed(eq(request), any());
        verify(logger).logBusinessWarning(eq("Notification send failed"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should throw exception for unsupported channel")
    void testUnsupportedChannel() {
        // Given
        NotificationRequest request = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            NotificationChannel.EMAIL,
            "admin@example.com",
            "Alert",
            "Device down",
            null,
            UUID.randomUUID()
        );
        
        when(repository.saveRequest(any())).thenReturn(request);
        when(sender.supportsChannel("EMAIL")).thenReturn(false);
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(request))
            .isInstanceOf(UnsupportedChannelException.class)
            .hasMessageContaining("EMAIL");
        
        verify(eventPublisher).publishNotificationRequested(request);
        verify(logger).logBusinessWarning(eq("Channel not supported by sender: EMAIL"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should validate notification request - null request")
    void testValidateNullRequest() {
        // Given
        NotificationRequest nullRequest = null;
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(nullRequest))
            .isInstanceOf(InvalidNotificationRequestException.class)
            .hasMessage("Notification request cannot be null");
    }
    
    @Test
    @DisplayName("Should validate notification request - empty recipient")
    void testValidateEmptyRecipient() {
        // Given
        NotificationRequest emptyRecipient = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            NotificationChannel.EMAIL,
            "",
            "Subject",
            "Message",
            null,
            UUID.randomUUID()
        );
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(emptyRecipient))
            .isInstanceOf(InvalidNotificationRequestException.class)
            .hasMessage("Recipient is required");
    }
    
    @Test
    @DisplayName("Should validate notification request - null type")
    void testValidateNullType() {
        // Given
        NotificationRequest nullType = new NotificationRequest(
            null,
            NotificationChannel.EMAIL,
            "admin@example.com",
            "Subject",
            "Message",
            null,
            UUID.randomUUID()
        );
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(nullType))
            .isInstanceOf(InvalidNotificationRequestException.class)
            .hasMessage("Notification type is required");
    }
    
    @Test
    @DisplayName("Should validate notification request - null channel")
    void testValidateNullChannel() {
        // Given
        NotificationRequest nullChannel = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            null,
            "admin@example.com",
            "Subject",
            "Message",
            null,
            UUID.randomUUID()
        );
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(nullChannel))
            .isInstanceOf(InvalidNotificationRequestException.class)
            .hasMessage("Notification channel is required");
    }
    
    @Test
    @DisplayName("Should validate notification request - empty message")
    void testValidateEmptyMessage() {
        // Given
        NotificationRequest emptyMessage = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            NotificationChannel.EMAIL,
            "admin@example.com",
            "Subject",
            "",
            null,
            UUID.randomUUID()
        );
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(emptyMessage))
            .isInstanceOf(InvalidNotificationRequestException.class)
            .hasMessage("Message is required");
    }
    
    @Test
    @DisplayName("Should validate email notification - missing subject")
    void testValidateEmailMissingSubject() {
        // Given
        NotificationRequest noSubject = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            NotificationChannel.EMAIL,
            "admin@example.com",
            null,
            "Message",
            null,
            UUID.randomUUID()
        );
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(noSubject))
            .isInstanceOf(InvalidNotificationRequestException.class)
            .hasMessage("Subject is required for email notifications");
    }
    
    @Test
    @DisplayName("Should validate email notification - invalid email format")
    void testValidateInvalidEmailFormat() {
        // Given
        NotificationRequest invalidEmail = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            NotificationChannel.EMAIL,
            "not-an-email",
            "Subject",
            "Message",
            null,
            UUID.randomUUID()
        );
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(invalidEmail))
            .isInstanceOf(InvalidNotificationRequestException.class)
            .hasMessageContaining("Invalid email recipient");
    }
    
    @Test
    @DisplayName("Should get notification history")
    void testGetNotificationHistory() {
        // Given
        Instant startTime = Instant.now().minusSeconds(3600);
        Instant endTime = Instant.now();
        List<NotificationRequest> expectedHistory = List.of(
            new NotificationRequest(
                NotificationType.DEVICE_DOWN,
                NotificationChannel.EMAIL,
                "admin@example.com",
                "Alert",
                "Message",
                null,
                UUID.randomUUID()
            )
        );
        
        when(repository.findRequestsByTimeRange(startTime, endTime)).thenReturn(expectedHistory);
        
        // When
        List<NotificationRequest> history = service.getNotificationHistory(startTime, endTime);
        
        // Then
        assertThat(history).isEqualTo(expectedHistory);
        verify(repository).findRequestsByTimeRange(startTime, endTime);
        verify(logger).logBusinessEvent(eq("Retrieving notification history"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should get failed notifications for retry")
    void testGetFailedNotifications() {
        // Given
        Instant startTime = Instant.now().minusSeconds(3600);
        Instant endTime = Instant.now();
        List<NotificationRequest> expectedFailed = List.of(
            new NotificationRequest(
                NotificationType.DEVICE_DOWN,
                NotificationChannel.EMAIL,
                "admin@example.com",
                "Alert",
                "Failed message",
                null,
                UUID.randomUUID()
            )
        );
        
        when(repository.findFailedRequestsByTimeRange(startTime, endTime)).thenReturn(expectedFailed);
        
        // When
        List<NotificationRequest> failed = service.getFailedNotifications(startTime, endTime);
        
        // Then
        assertThat(failed).isEqualTo(expectedFailed);
        verify(repository).findFailedRequestsByTimeRange(startTime, endTime);
        verify(logger).logBusinessEvent(eq("Retrieving failed notifications"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should get notifications by source event ID")
    void testGetNotificationsBySourceEvent() {
        // Given
        UUID sourceEventId = UUID.randomUUID();
        List<NotificationRequest> expectedNotifications = List.of(
            new NotificationRequest(
                NotificationType.DEVICE_DOWN,
                NotificationChannel.EMAIL,
                "admin@example.com",
                "Alert",
                "Related to event",
                null,
                sourceEventId
            )
        );
        
        when(repository.findRequestsBySourceEventId(sourceEventId)).thenReturn(expectedNotifications);
        
        // When
        List<NotificationRequest> notifications = service.getNotificationsBySourceEvent(sourceEventId);
        
        // Then
        assertThat(notifications).isEqualTo(expectedNotifications);
        verify(repository).findRequestsBySourceEventId(sourceEventId);
        verify(logger).logBusinessEvent(eq("Retrieving notifications by source event"), any(Map.class));
    }
    
    @Test
    @DisplayName("Should handle NotificationSendException correctly")
    void testHandleNotificationSendException() {
        // Given
        NotificationRequest request = new NotificationRequest(
            NotificationType.DEVICE_DOWN,
            NotificationChannel.EMAIL,
            "admin@example.com",
            "Alert",
            "Device down",
            null,
            UUID.randomUUID()
        );
        
        when(repository.saveRequest(any())).thenReturn(request);
        when(sender.supportsChannel("EMAIL")).thenReturn(true);
        when(sender.send(any())).thenThrow(
            new NotificationSendException("Email server error", "EMAIL", "admin@example.com", null)
        );
        
        // When & Then
        assertThatThrownBy(() -> service.sendNotification(request))
            .isInstanceOf(NotificationSendException.class)
            .hasMessageContaining("Email server error");
        
        verify(eventPublisher).publishNotificationRequested(request);
    }
}