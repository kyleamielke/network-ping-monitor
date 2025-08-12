package io.thatworked.support.notification.infrastructure.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.application.dto.SendNotificationCommand;
import io.thatworked.support.notification.application.usecase.SendNotificationUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AlertLifecycleConsumer Tests")
class AlertLifecycleConsumerTest {
    
    @Mock
    private StructuredLoggerFactory loggerFactory;
    
    @Mock
    private StructuredLogger logger;
    
    @Mock
    private StructuredLogger.ContextBuilder contextBuilder;
    
    @Mock
    private SendNotificationUseCase sendNotificationUseCase;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private DateTimeFormatter dateTimeFormatter;
    
    @Mock
    private Acknowledgment acknowledgment;
    
    private AlertLifecycleConsumer consumer;
    
    @BeforeEach
    void setUp() {
        // Mock logger
        when(loggerFactory.getLogger(any(Class.class))).thenReturn(logger);
        when(logger.with(anyString(), any())).thenReturn(contextBuilder);
        when(contextBuilder.with(anyString(), any())).thenReturn(contextBuilder);
        doNothing().when(contextBuilder).info(anyString());
        doNothing().when(contextBuilder).error(anyString(), any());
        
        // Mock date formatter
        when(dateTimeFormatter.format(any())).thenReturn("2024-01-15 10:30:00");
        
        consumer = new AlertLifecycleConsumer(loggerFactory, sendNotificationUseCase, objectMapper, dateTimeFormatter);
        
        // Set the alert recipient via reflection
        ReflectionTestUtils.setField(consumer, "alertRecipient", "admin@example.com");
    }
    
    @Test
    @DisplayName("Should process ALERT_CREATED event and send notification")
    void testProcessAlertCreatedEvent() throws Exception {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        String deviceName = "test-device";
        String ipAddress = "192.168.1.100";
        
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventType", "ALERT_CREATED");
        eventMap.put("id", alertId.toString());
        eventMap.put("deviceId", deviceId.toString());
        eventMap.put("deviceName", deviceName);
        eventMap.put("alertType", "DEVICE_DOWN");
        eventMap.put("message", "Device is not responding to ping");
        eventMap.put("ipAddress", ipAddress);
        eventMap.put("consecutiveFailures", 3);
        eventMap.put("failureReason", "Request timeout");
        eventMap.put("timestamp", Instant.now().getEpochSecond());
        
        String eventJson = "{\"eventType\":\"ALERT_CREATED\"}";
        
        when(objectMapper.readValue(eventJson, Map.class)).thenReturn(eventMap);
        
        // When
        consumer.handleAlertLifecycleEvent(eventJson, alertId.toString(), "alert-lifecycle-events", 0, 100L, acknowledgment);
        
        // Then
        ArgumentCaptor<SendNotificationCommand> commandCaptor = ArgumentCaptor.forClass(SendNotificationCommand.class);
        verify(sendNotificationUseCase).execute(commandCaptor.capture());
        
        SendNotificationCommand command = commandCaptor.getValue();
        assertThat(command.getNotificationType()).isEqualTo("DEVICE_DOWN");
        assertThat(command.getChannel()).isEqualTo("EMAIL");
        assertThat(command.getRecipient()).isEqualTo("admin@example.com");
        assertThat(command.getSubject()).contains(deviceName);
        assertThat(command.getSubject()).contains("DEVICE_DOWN");
        assertThat(command.getMessage()).contains(deviceName);
        assertThat(command.getMessage()).contains("Device is not responding to ping");
        
        Map<String, Object> metadata = command.getMetadata();
        assertThat(metadata.get("alertId")).isEqualTo(alertId);
        assertThat(metadata.get("deviceId")).isEqualTo(deviceId);
        assertThat(metadata.get("deviceName")).isEqualTo(deviceName);
        assertThat(metadata.get("ipAddress")).isEqualTo(ipAddress);
        assertThat(metadata.get("consecutiveFailures")).isEqualTo(3);
        
        verify(acknowledgment).acknowledge();
    }
    
    @Test
    @DisplayName("Should process ALERT_RESOLVED event and send notification")
    void testProcessAlertResolvedEvent() throws Exception {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        String deviceName = "test-device";
        
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventType", "ALERT_RESOLVED");
        eventMap.put("id", alertId.toString());
        eventMap.put("deviceId", deviceId.toString());
        eventMap.put("deviceName", deviceName);
        eventMap.put("alertType", "DEVICE_DOWN");
        eventMap.put("timestamp", Instant.now().getEpochSecond());
        
        String eventJson = "{\"eventType\":\"ALERT_RESOLVED\"}";
        
        when(objectMapper.readValue(eventJson, Map.class)).thenReturn(eventMap);
        
        // When
        consumer.handleAlertLifecycleEvent(eventJson, alertId.toString(), "alert-lifecycle-events", 0, 100L, acknowledgment);
        
        // Then
        ArgumentCaptor<SendNotificationCommand> commandCaptor = ArgumentCaptor.forClass(SendNotificationCommand.class);
        verify(sendNotificationUseCase).execute(commandCaptor.capture());
        
        SendNotificationCommand command = commandCaptor.getValue();
        assertThat(command.getNotificationType()).isEqualTo("DEVICE_RECOVERED");
        assertThat(command.getChannel()).isEqualTo("EMAIL");
        assertThat(command.getRecipient()).isEqualTo("admin@example.com");
        assertThat(command.getSubject()).contains("Resolved");
        assertThat(command.getSubject()).contains(deviceName);
        assertThat(command.getMessage()).contains("Alert resolved");
        assertThat(command.getMessage()).contains(deviceName);
        
        Map<String, Object> metadata = command.getMetadata();
        assertThat(metadata.get("alertId")).isEqualTo(alertId);
        assertThat(metadata.get("deviceId")).isEqualTo(deviceId);
        assertThat(metadata.get("deviceName")).isEqualTo(deviceName);
        
        verify(acknowledgment).acknowledge();
    }
    
    @Test
    @DisplayName("Should ignore ALERT_ACKNOWLEDGED event")
    void testIgnoreAlertAcknowledgedEvent() throws Exception {
        // Given
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventType", "ALERT_ACKNOWLEDGED");
        eventMap.put("id", UUID.randomUUID().toString());
        
        String eventJson = "{\"eventType\":\"ALERT_ACKNOWLEDGED\"}";
        
        when(objectMapper.readValue(eventJson, Map.class)).thenReturn(eventMap);
        
        // When
        consumer.handleAlertLifecycleEvent(eventJson, "key", "alert-lifecycle-events", 0, 100L, acknowledgment);
        
        // Then
        verify(sendNotificationUseCase, never()).execute(any());
        verify(acknowledgment).acknowledge();
    }
    
    @Test
    @DisplayName("Should handle malformed JSON gracefully")
    void testHandleMalformedJson() throws Exception {
        // Given
        String malformedJson = "{invalid json";
        
        when(objectMapper.readValue(malformedJson, Map.class))
            .thenThrow(new RuntimeException("Invalid JSON"));
        
        // When
        consumer.handleAlertLifecycleEvent(malformedJson, "key", "alert-lifecycle-events", 0, 100L, acknowledgment);
        
        // Then
        verify(sendNotificationUseCase, never()).execute(any());
        verify(acknowledgment).acknowledge(); // Still acknowledge to prevent reprocessing
        verify(contextBuilder).error(eq("Failed to process alert lifecycle event"), any());
    }
    
    @Test
    @DisplayName("Should handle notification send failure gracefully")
    void testHandleNotificationSendFailure() throws Exception {
        // Given
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventType", "ALERT_CREATED");
        eventMap.put("id", UUID.randomUUID().toString());
        eventMap.put("deviceId", UUID.randomUUID().toString());
        eventMap.put("deviceName", "test-device");
        eventMap.put("alertType", "DEVICE_DOWN");
        eventMap.put("message", "Device down");
        eventMap.put("timestamp", Instant.now().getEpochSecond());
        
        String eventJson = "{\"eventType\":\"ALERT_CREATED\"}";
        
        when(objectMapper.readValue(eventJson, Map.class)).thenReturn(eventMap);
        doThrow(new RuntimeException("Notification service error"))
            .when(sendNotificationUseCase).execute(any());
        
        // When
        consumer.handleAlertLifecycleEvent(eventJson, "key", "alert-lifecycle-events", 0, 100L, acknowledgment);
        
        // Then
        verify(acknowledgment).acknowledge(); // Still acknowledge
        verify(contextBuilder).error(eq("Failed to process alert lifecycle event"), any());
    }
    
    @Test
    @DisplayName("Should handle missing required fields")
    void testHandleMissingFields() throws Exception {
        // Given - missing deviceId
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventType", "ALERT_CREATED");
        eventMap.put("id", UUID.randomUUID().toString());
        // deviceId is missing - should cause UUID parsing error
        eventMap.put("deviceName", "test-device");
        eventMap.put("alertType", "DEVICE_DOWN");
        eventMap.put("timestamp", Instant.now().getEpochSecond());
        
        String eventJson = "{\"eventType\":\"ALERT_CREATED\"}";
        
        when(objectMapper.readValue(eventJson, Map.class)).thenReturn(eventMap);
        
        // When
        consumer.handleAlertLifecycleEvent(eventJson, "key", "alert-lifecycle-events", 0, 100L, acknowledgment);
        
        // Then
        verify(acknowledgment).acknowledge();
        verify(contextBuilder).error(eq("Failed to process alert lifecycle event"), any());
    }
    
    @Test
    @DisplayName("Should parse numeric timestamp correctly")
    void testParseNumericTimestamp() throws Exception {
        // Given
        double timestampWithFraction = 1705308600.123456789;
        
        Map<String, Object> eventMap = new HashMap<>();
        eventMap.put("eventType", "ALERT_CREATED");
        eventMap.put("id", UUID.randomUUID().toString());
        eventMap.put("deviceId", UUID.randomUUID().toString());
        eventMap.put("deviceName", "test-device");
        eventMap.put("alertType", "DEVICE_DOWN");
        eventMap.put("message", "Device down");
        eventMap.put("timestamp", timestampWithFraction);
        
        String eventJson = "{\"eventType\":\"ALERT_CREATED\"}";
        
        when(objectMapper.readValue(eventJson, Map.class)).thenReturn(eventMap);
        
        // When
        consumer.handleAlertLifecycleEvent(eventJson, "key", "alert-lifecycle-events", 0, 100L, acknowledgment);
        
        // Then
        verify(sendNotificationUseCase).execute(any());
        verify(acknowledgment).acknowledge();
    }
}