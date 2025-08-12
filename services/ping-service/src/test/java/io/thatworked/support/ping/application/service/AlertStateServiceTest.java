package io.thatworked.support.ping.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.ping.api.dto.PingResultDTO;
import io.thatworked.support.ping.domain.AlertState;
import io.thatworked.support.ping.domain.MonitoredDevice;
import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.domain.PingStatus;
import io.thatworked.support.ping.infrastructure.config.KafkaConfig;
import io.thatworked.support.ping.infrastructure.event.PingResultEvent;
import io.thatworked.support.ping.infrastructure.event.alert.DeviceDownEvent;
import io.thatworked.support.ping.infrastructure.event.alert.DeviceRecoveredEvent;
import io.thatworked.support.ping.infrastructure.repository.jpa.AlertStateRepository;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingTargetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AlertStateService Tests - Critical Alert Threshold Logic")
class AlertStateServiceTest {
    
    @Mock
    private StructuredLoggerFactory structuredLoggerFactory;
    
    @Mock
    private StructuredLogger logger;
    
    @Mock
    private StructuredLogger.ContextBuilder contextBuilder;
    
    @Mock
    private AlertStateRepository alertStateRepository;
    
    @Mock
    private PingTargetRepository pingTargetRepository;
    
    @Mock
    private MonitoredDeviceService monitoredDeviceService;
    
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @Captor
    private ArgumentCaptor<AlertState> alertStateCaptor;
    
    @Captor
    private ArgumentCaptor<Object> kafkaEventCaptor;
    
    @Captor
    private ArgumentCaptor<String> kafkaTopicCaptor;
    
    private AlertStateService service;
    
    private static final int FAILURE_THRESHOLD = 3;
    private static final int RECOVERY_THRESHOLD = 2;
    
    @BeforeEach
    void setUp() {
        when(structuredLoggerFactory.getLogger(any())).thenReturn(logger);
        when(logger.with(anyString(), any())).thenReturn(contextBuilder);
        when(contextBuilder.with(anyString(), any())).thenReturn(contextBuilder);
        doNothing().when(contextBuilder).info(anyString());
        doNothing().when(contextBuilder).debug(anyString());
        doNothing().when(contextBuilder).warn(anyString());
        doNothing().when(contextBuilder).error(anyString(), any(Throwable.class));
        
        service = new AlertStateService(
            structuredLoggerFactory,
            alertStateRepository,
            pingTargetRepository,
            monitoredDeviceService,
            kafkaTemplate,
            objectMapper
        );
        
        // Set thresholds using reflection
        ReflectionTestUtils.setField(service, "failureThreshold", FAILURE_THRESHOLD);
        ReflectionTestUtils.setField(service, "recoveryThreshold", RECOVERY_THRESHOLD);
        ReflectionTestUtils.setField(service, "alertingEnabled", true);
    }
    
    @Test
    @DisplayName("Should trigger DOWN alert after 3 consecutive failures")
    void testTriggerDownAlertAfterThreeFailures() {
        // Given
        UUID deviceId = UUID.randomUUID();
        MonitoredDevice device = createMonitoredDevice(deviceId, "Server-1", "192.168.1.100");
        AlertState alertState = new AlertState();
        alertState.setDeviceId(deviceId);
        alertState.setConsecutiveFailures(2); // Already had 2 failures
        alertState.setAlerting(false);
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertState));
        when(monitoredDeviceService.findById(deviceId)).thenReturn(Optional.of(device));
        
        PingResult failedPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.FAILURE)
            .build();
        
        PingResultEvent event = new PingResultEvent(failedPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getConsecutiveFailures()).isEqualTo(3);
        assertThat(savedState.isAlerting()).isTrue();
        
        // Verify DOWN event was published
        verify(kafkaTemplate, atLeast(2)).send(kafkaTopicCaptor.capture(), eq(deviceId.toString()), kafkaEventCaptor.capture());
        
        // Find the DeviceDownEvent
        boolean foundDownEvent = kafkaEventCaptor.getAllValues().stream()
            .anyMatch(e -> e instanceof DeviceDownEvent);
        assertThat(foundDownEvent).isTrue();
        
        verify(contextBuilder).warn("Device is down after consecutive failures");
    }
    
    @Test
    @DisplayName("Should NOT trigger alert before reaching failure threshold")
    void testNoAlertBeforeThreshold() {
        // Given
        UUID deviceId = UUID.randomUUID();
        AlertState alertState = new AlertState();
        alertState.setDeviceId(deviceId);
        alertState.setConsecutiveFailures(1); // Only 1 failure so far
        alertState.setAlerting(false);
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertState));
        
        PingResult failedPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.FAILURE)
            .build();
        
        PingResultEvent event = new PingResultEvent(failedPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getConsecutiveFailures()).isEqualTo(2);
        assertThat(savedState.isAlerting()).isFalse(); // Still not alerting
        
        // Verify NO DeviceDownEvent was published
        verify(kafkaTemplate, never()).send(eq(KafkaConfig.DEVICE_DOWN_TOPIC), anyString(), any(DeviceDownEvent.class));
    }
    
    @Test
    @DisplayName("Should trigger RECOVERY after 2 consecutive successes following alert")
    void testTriggerRecoveryAfterTwoSuccesses() {
        // Given
        UUID deviceId = UUID.randomUUID();
        MonitoredDevice device = createMonitoredDevice(deviceId, "Server-1", "192.168.1.100");
        AlertState alertState = new AlertState();
        alertState.setDeviceId(deviceId);
        alertState.setConsecutiveSuccesses(1); // Already had 1 success
        alertState.setAlerting(true); // Currently in alert state
        alertState.setLastFailureTime(Instant.now().minusSeconds(300));
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertState));
        when(monitoredDeviceService.findById(deviceId)).thenReturn(Optional.of(device));
        
        PingResult successPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.SUCCESS)
            .roundTripTime(25.5)
            .build();
        
        PingResultEvent event = new PingResultEvent(successPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getConsecutiveSuccesses()).isEqualTo(2);
        assertThat(savedState.isAlerting()).isFalse(); // No longer alerting
        
        // Verify RECOVERY event was published
        verify(kafkaTemplate, atLeast(2)).send(kafkaTopicCaptor.capture(), eq(deviceId.toString()), kafkaEventCaptor.capture());
        
        boolean foundRecoveryEvent = kafkaEventCaptor.getAllValues().stream()
            .anyMatch(e -> e instanceof DeviceRecoveredEvent);
        assertThat(foundRecoveryEvent).isTrue();
        
        verify(contextBuilder).info("Device has recovered after consecutive successes");
    }
    
    @Test
    @DisplayName("Should NOT trigger recovery before reaching recovery threshold")
    void testNoRecoveryBeforeThreshold() {
        // Given
        UUID deviceId = UUID.randomUUID();
        AlertState alertState = new AlertState();
        alertState.setDeviceId(deviceId);
        alertState.setConsecutiveSuccesses(0); // No successes yet
        alertState.setAlerting(true); // Currently alerting
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertState));
        
        PingResult successPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.SUCCESS)
            .roundTripTime(20.0)
            .build();
        
        PingResultEvent event = new PingResultEvent(successPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getConsecutiveSuccesses()).isEqualTo(1);
        assertThat(savedState.isAlerting()).isTrue(); // Still alerting
        
        // Verify NO DeviceRecoveredEvent was published
        verify(kafkaTemplate, never()).send(eq(KafkaConfig.DEVICE_RECOVERED_TOPIC), anyString(), any(DeviceRecoveredEvent.class));
    }
    
    @Test
    @DisplayName("Should reset success counter when ping fails")
    void testResetSuccessCounterOnFailure() {
        // Given
        UUID deviceId = UUID.randomUUID();
        AlertState alertState = new AlertState();
        alertState.setDeviceId(deviceId);
        alertState.setConsecutiveSuccesses(5); // Had 5 successes
        alertState.setConsecutiveFailures(0);
        alertState.setAlerting(false);
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertState));
        
        PingResult failedPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.TIMEOUT)
            .build();
        
        PingResultEvent event = new PingResultEvent(failedPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getConsecutiveSuccesses()).isEqualTo(0); // Reset to 0
        assertThat(savedState.getConsecutiveFailures()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should reset failure counter when ping succeeds")
    void testResetFailureCounterOnSuccess() {
        // Given
        UUID deviceId = UUID.randomUUID();
        AlertState alertState = new AlertState();
        alertState.setDeviceId(deviceId);
        alertState.setConsecutiveFailures(2); // Had 2 failures
        alertState.setConsecutiveSuccesses(0);
        alertState.setAlerting(false);
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertState));
        
        PingResult successPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.SUCCESS)
            .roundTripTime(15.0)
            .build();
        
        PingResultEvent event = new PingResultEvent(successPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getConsecutiveFailures()).isEqualTo(0); // Reset to 0
        assertThat(savedState.getConsecutiveSuccesses()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should create new AlertState for first-time device")
    void testCreateNewAlertStateForNewDevice() {
        // Given
        UUID deviceId = UUID.randomUUID();
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.empty());
        
        PingResult successPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.SUCCESS)
            .roundTripTime(10.0)
            .build();
        
        PingResultEvent event = new PingResultEvent(successPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getDeviceId()).isEqualTo(deviceId);
        assertThat(savedState.getConsecutiveSuccesses()).isEqualTo(1);
        assertThat(savedState.getConsecutiveFailures()).isEqualTo(0);
        assertThat(savedState.isAlerting()).isFalse();
    }
    
    @Test
    @DisplayName("Should publish baseline healthy event for new healthy device")
    void testPublishBaselineHealthyEvent() {
        // Given
        UUID deviceId = UUID.randomUUID();
        MonitoredDevice device = createMonitoredDevice(deviceId, "Server-1", "192.168.1.100");
        AlertState alertState = new AlertState();
        alertState.setDeviceId(deviceId);
        alertState.setConsecutiveSuccesses(0);
        alertState.setAlerting(false);
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertState));
        when(monitoredDeviceService.findById(deviceId)).thenReturn(Optional.of(device));
        
        PingResult successPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.SUCCESS)
            .roundTripTime(12.0)
            .build();
        
        PingResultEvent event = new PingResultEvent(successPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getConsecutiveSuccesses()).isEqualTo(1);
        
        // Should publish baseline healthy event
        verify(contextBuilder).debug("Published baseline healthy event for device");
    }
    
    @Test
    @DisplayName("Should handle alerting disabled configuration")
    void testAlertingDisabled() {
        // Given
        ReflectionTestUtils.setField(service, "alertingEnabled", false);
        UUID deviceId = UUID.randomUUID();
        
        PingResult failedPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.FAILURE)
            .build();
        
        PingResultEvent event = new PingResultEvent(failedPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        // Should still publish ping result event
        verify(kafkaTemplate, atLeastOnce()).send(anyString(), anyString(), any());
        // But should not process alert state
        verify(alertStateRepository, never()).findById(any());
        verify(alertStateRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should handle edge case: flapping device behavior")
    void testFlappingDeviceBehavior() {
        // Given
        UUID deviceId = UUID.randomUUID();
        MonitoredDevice device = createMonitoredDevice(deviceId, "Flapping-Server", "192.168.1.100");
        when(monitoredDeviceService.findById(deviceId)).thenReturn(Optional.of(device));
        
        // Simulate flapping: fail, fail, fail (alert), success, fail, success, success (recovery)
        
        // Start with no alert state
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.empty());
        
        // First failure
        PingResult fail1 = createPingResult(deviceId, PingStatus.FAILURE);
        service.handlePingResult(new PingResultEvent(fail1));
        verify(alertStateRepository, times(1)).save(alertStateCaptor.capture());
        assertThat(alertStateCaptor.getValue().getConsecutiveFailures()).isEqualTo(1);
        
        // Update mock to return saved state
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertStateCaptor.getValue()));
        
        // Second failure
        PingResult fail2 = createPingResult(deviceId, PingStatus.FAILURE);
        service.handlePingResult(new PingResultEvent(fail2));
        verify(alertStateRepository, times(2)).save(alertStateCaptor.capture());
        assertThat(alertStateCaptor.getValue().getConsecutiveFailures()).isEqualTo(2);
        
        // Update mock
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertStateCaptor.getValue()));
        
        // Third failure - should trigger alert
        PingResult fail3 = createPingResult(deviceId, PingStatus.FAILURE);
        service.handlePingResult(new PingResultEvent(fail3));
        verify(alertStateRepository, times(3)).save(alertStateCaptor.capture());
        assertThat(alertStateCaptor.getValue().getConsecutiveFailures()).isEqualTo(3);
        assertThat(alertStateCaptor.getValue().isAlerting()).isTrue();
        
        // Update mock
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertStateCaptor.getValue()));
        
        // One success - not enough for recovery
        PingResult success1 = createPingResult(deviceId, PingStatus.SUCCESS);
        service.handlePingResult(new PingResultEvent(success1));
        verify(alertStateRepository, times(4)).save(alertStateCaptor.capture());
        assertThat(alertStateCaptor.getValue().getConsecutiveSuccesses()).isEqualTo(1);
        assertThat(alertStateCaptor.getValue().isAlerting()).isTrue(); // Still alerting
        
        // Update mock
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertStateCaptor.getValue()));
        
        // Another failure - resets success counter
        PingResult fail4 = createPingResult(deviceId, PingStatus.FAILURE);
        service.handlePingResult(new PingResultEvent(fail4));
        verify(alertStateRepository, times(5)).save(alertStateCaptor.capture());
        assertThat(alertStateCaptor.getValue().getConsecutiveSuccesses()).isEqualTo(0);
        assertThat(alertStateCaptor.getValue().getConsecutiveFailures()).isEqualTo(1);
        assertThat(alertStateCaptor.getValue().isAlerting()).isTrue(); // Still alerting
        
        // Update mock
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertStateCaptor.getValue()));
        
        // Two successes for recovery
        PingResult success2 = createPingResult(deviceId, PingStatus.SUCCESS);
        service.handlePingResult(new PingResultEvent(success2));
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertStateCaptor.getValue()));
        
        PingResult success3 = createPingResult(deviceId, PingStatus.SUCCESS);
        service.handlePingResult(new PingResultEvent(success3));
        
        verify(alertStateRepository, times(7)).save(alertStateCaptor.capture());
        assertThat(alertStateCaptor.getValue().getConsecutiveSuccesses()).isEqualTo(2);
        assertThat(alertStateCaptor.getValue().isAlerting()).isFalse(); // Recovered!
    }
    
    @Test
    @DisplayName("Should handle null device information gracefully")
    void testHandleNullDeviceInformation() {
        // Given
        UUID deviceId = UUID.randomUUID();
        AlertState alertState = new AlertState();
        alertState.setDeviceId(deviceId);
        alertState.setConsecutiveFailures(2);
        alertState.setAlerting(false);
        
        when(alertStateRepository.findById(deviceId)).thenReturn(Optional.of(alertState));
        when(monitoredDeviceService.findById(deviceId)).thenReturn(Optional.empty()); // No device found
        
        PingResult failedPing = PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(PingStatus.FAILURE)
            .build();
        
        PingResultEvent event = new PingResultEvent(failedPing);
        
        // When
        service.handlePingResult(event);
        
        // Then
        verify(alertStateRepository).save(alertStateCaptor.capture());
        AlertState savedState = alertStateCaptor.getValue();
        assertThat(savedState.getConsecutiveFailures()).isEqualTo(3);
        assertThat(savedState.isAlerting()).isTrue();
        
        // Should still publish event with "Unknown" values
        verify(kafkaTemplate, atLeast(1)).send(anyString(), eq(deviceId.toString()), kafkaEventCaptor.capture());
    }
    
    // Helper methods
    private MonitoredDevice createMonitoredDevice(UUID deviceId, String name, String ipAddress) {
        MonitoredDevice device = new MonitoredDevice();
        device.setDeviceId(deviceId);
        device.setDeviceName(name);
        device.setIpAddress(ipAddress);
        device.setOs("Linux");
        device.setOsType("Ubuntu");
        return device;
    }
    
    private PingResult createPingResult(UUID deviceId, PingStatus status) {
        return PingResult.builder()
            .deviceId(deviceId)
            .time(Instant.now())
            .status(status)
            .roundTripTime(status.isSuccess() ? 15.0 : null)
            .build();
    }
}