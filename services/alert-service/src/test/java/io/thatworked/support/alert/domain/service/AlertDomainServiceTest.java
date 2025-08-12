package io.thatworked.support.alert.domain.service;

import io.thatworked.support.alert.config.BusinessRulesConfig;
import io.thatworked.support.alert.config.MessagesConfig;
import io.thatworked.support.alert.domain.exception.AlertDomainException;
import io.thatworked.support.alert.domain.exception.AlertNotFoundException;
import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.port.AlertRepository;
import io.thatworked.support.alert.domain.port.DomainLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AlertDomainService Tests")
class AlertDomainServiceTest {

    @Mock
    private AlertRepository repository;
    
    @Mock
    private DomainLogger domainLogger;
    
    @Mock
    private BusinessRulesConfig businessRulesConfig;
    
    @Mock
    private MessagesConfig messagesConfig;
    
    @Mock
    private BusinessRulesConfig.Lifecycle lifecycleConfig;
    
    @Mock
    private MessagesConfig.Errors errorMessages;
    
    private AlertDomainService service;
    
    @BeforeEach
    void setUp() {
        when(businessRulesConfig.getLifecycle()).thenReturn(lifecycleConfig);
        when(messagesConfig.getErrors()).thenReturn(errorMessages);
        when(lifecycleConfig.getMaxActiveAlertsPerDevice()).thenReturn(10);
        when(errorMessages.getDeviceMaxAlerts()).thenReturn("Device has reached maximum of %d active alerts");
        
        service = new AlertDomainService(repository, domainLogger, businessRulesConfig, messagesConfig);
    }
    
    @Test
    @DisplayName("Should create alert successfully")
    void testCreateAlert() {
        // Given
        UUID deviceId = UUID.randomUUID();
        String deviceName = "test-device";
        String message = "Device test-device is DOWN";
        
        when(repository.findUnresolvedByDeviceId(deviceId)).thenReturn(Arrays.asList());
        
        AlertDomain savedAlert = new AlertDomain(
            deviceId, deviceName, AlertType.DEVICE_DOWN, 
            message
        );
        
        when(repository.save(any(AlertDomain.class))).thenReturn(savedAlert);
        
        // When
        AlertDomain result = service.createAlert(deviceId, deviceName, AlertType.DEVICE_DOWN, message);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeviceId()).isEqualTo(deviceId);
        assertThat(result.getDeviceName()).isEqualTo(deviceName);
        assertThat(result.getAlertType()).isEqualTo(AlertType.DEVICE_DOWN);
        assertThat(result.getMessage()).isEqualTo(message);
        assertThat(result.isResolved()).isFalse();
        assertThat(result.isAcknowledged()).isFalse();
        
        verify(repository).findUnresolvedByDeviceId(deviceId);
        verify(repository).save(any(AlertDomain.class));
    }
    
    @Test
    @DisplayName("Should auto-resolve device down alerts when device recovers")
    void testAutoResolveDeviceAlerts() {
        // Given
        UUID deviceId = UUID.randomUUID();
        
        AlertDomain alert1 = new AlertDomain(
            UUID.randomUUID(), deviceId, "test-device", AlertType.DEVICE_DOWN,
            "Device is down", Instant.now(), false, null,
            false, null, null,
            Instant.now(), Instant.now(), "192.168.1.100",
            3, "Ping timeout", 1L
        );
        
        AlertDomain alert2 = new AlertDomain(
            UUID.randomUUID(), deviceId, "test-device", AlertType.DEVICE_DOWN,
            "Device is down again", Instant.now(), false, null,
            true, Instant.now(), "admin", // This one is acknowledged
            Instant.now(), Instant.now(), "192.168.1.100",
            3, "Ping timeout", 1L
        );
        
        List<AlertDomain> unresolvedAlerts = Arrays.asList(alert1, alert2);
        
        when(repository.findUnresolvedByDeviceId(deviceId)).thenReturn(unresolvedAlerts);
        when(repository.save(any(AlertDomain.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        List<AlertDomain> result = service.autoResolveDeviceAlerts(deviceId);
        
        // Then
        assertThat(alert1.isResolved()).isTrue();
        assertThat(alert2.isResolved()).isTrue();
        
        verify(repository).findUnresolvedByDeviceId(deviceId);
        verify(repository, times(2)).save(any(AlertDomain.class));
    }
    
    @Test
    @DisplayName("Should acknowledge alert successfully")
    void testAcknowledgeAlert() {
        // Given
        UUID alertId = UUID.randomUUID();
        String acknowledgedBy = "admin@example.com";
        
        AlertDomain alert = new AlertDomain(
            alertId, UUID.randomUUID(), "test-device", AlertType.DEVICE_DOWN,
            "Device is down", Instant.now(), false, null,
            false, null, null,
            Instant.now(), Instant.now(), "192.168.1.100",
            3, "Ping timeout", 1L
        );
        
        when(repository.findById(alertId)).thenReturn(Optional.of(alert));
        when(repository.save(alert)).thenReturn(alert);
        
        // When
        AlertDomain result = service.acknowledgeAlert(alertId, acknowledgedBy);
        
        // Then
        assertThat(result.isAcknowledged()).isTrue();
        assertThat(result.getAcknowledgedBy()).isEqualTo(acknowledgedBy);
        assertThat(result.getAcknowledgedAt()).isNotNull();
        
        verify(repository).findById(alertId);
        verify(repository).save(alert);
    }
    
    @Test
    @DisplayName("Should throw exception when acknowledging already acknowledged alert")
    void testAcknowledgeAlreadyAcknowledgedAlert() {
        // Given
        UUID alertId = UUID.randomUUID();
        
        AlertDomain alert = new AlertDomain(
            alertId, UUID.randomUUID(), "test-device", AlertType.DEVICE_DOWN,
            "Device is down", Instant.now(), false, null,
            true, Instant.now(), "other-admin", // Already acknowledged
            Instant.now(), Instant.now(), "192.168.1.100",
            3, "Ping timeout", 1L
        );
        
        when(repository.findById(alertId)).thenReturn(Optional.of(alert));
        when(errorMessages.getAlertNotFound()).thenReturn("Alert not found: %s");
        
        // When & Then
        assertThatThrownBy(() -> service.acknowledgeAlert(alertId, "admin"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("already acknowledged");
        
        verify(repository).findById(alertId);
        verify(repository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should resolve alert manually")
    void testResolveAlert() {
        // Given
        UUID alertId = UUID.randomUUID();
        
        AlertDomain alert = new AlertDomain(
            alertId, UUID.randomUUID(), "test-device", AlertType.DEVICE_DOWN,
            "Device is down", Instant.now(), false, null,
            false, null, null,
            Instant.now(), Instant.now(), "192.168.1.100",
            3, "Ping timeout", 1L
        );
        
        when(repository.findById(alertId)).thenReturn(Optional.of(alert));
        when(repository.save(alert)).thenReturn(alert);
        
        // When
        AlertDomain result = service.resolveAlert(alertId);
        
        // Then
        assertThat(result.isResolved()).isTrue();
        assertThat(result.getResolvedAt()).isNotNull();
        
        verify(repository).findById(alertId);
        verify(repository).save(alert);
    }
    
    @Test
    @DisplayName("Should throw exception when resolving non-existent alert")
    void testResolveNonExistentAlert() {
        // Given
        UUID alertId = UUID.randomUUID();
        when(repository.findById(alertId)).thenReturn(Optional.empty());
        when(errorMessages.getAlertNotFound()).thenReturn("Alert not found: %s");
        
        // When & Then
        assertThatThrownBy(() -> service.resolveAlert(alertId))
            .isInstanceOf(AlertNotFoundException.class)
            .hasMessageContaining(alertId.toString());
        
        verify(repository).findById(alertId);
        verify(repository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should cleanup old alerts based on retention policy")
    void testCleanupOldAlerts() {
        // Given
        when(lifecycleConfig.getRetentionPeriodDays()).thenReturn(30);
        
        Instant cutoffDate = Instant.now().minus(30, java.time.temporal.ChronoUnit.DAYS);
        
        AlertDomain oldAlert1 = new AlertDomain(UUID.randomUUID(), "device1", AlertType.DEVICE_DOWN, "Old Alert 1");
        AlertDomain oldAlert2 = new AlertDomain(UUID.randomUUID(), "device2", AlertType.DEVICE_DOWN, "Old Alert 2");
        
        List<AlertDomain> oldAlerts = Arrays.asList(oldAlert1, oldAlert2);
        
        when(repository.findByTimestampBetween(any(Instant.class), any(Instant.class))).thenReturn(oldAlerts);
        
        // When
        int deletedCount = service.cleanupOldAlerts();
        
        // Then
        assertThat(deletedCount).isEqualTo(2);
        verify(repository, times(2)).deleteById(any(UUID.class));
    }
    
    @Test
    @DisplayName("Should get alert statistics")
    void testGetAlertStatistics() {
        // Given
        when(repository.count()).thenReturn(100L);
        when(repository.countUnresolved()).thenReturn(25L);
        when(repository.countUnacknowledged()).thenReturn(15L);
        
        // When
        AlertDomainService.AlertStatistics stats = service.getStatistics();
        
        // Then
        assertThat(stats.getTotalAlerts()).isEqualTo(100L);
        assertThat(stats.getUnresolvedAlerts()).isEqualTo(25L);
        assertThat(stats.getUnacknowledgedAlerts()).isEqualTo(15L);
        
        verify(repository).count();
        verify(repository).countUnresolved();
        verify(repository).countUnacknowledged();
    }
    
    @Test
    @DisplayName("Should create alert with metadata")
    void testCreateAlertWithMetadata() {
        // Given
        UUID deviceId = UUID.randomUUID();
        String deviceName = "test-device";
        String message = "Device test-device is DOWN";
        String ipAddress = "192.168.1.100";
        Integer consecutiveFailures = 3;
        String failureReason = "Ping timeout";
        
        when(repository.findUnresolvedByDeviceId(deviceId)).thenReturn(Arrays.asList());
        
        AlertDomain savedAlert = new AlertDomain(
            deviceId, deviceName, AlertType.DEVICE_DOWN, 
            message, ipAddress, consecutiveFailures, failureReason
        );
        
        when(repository.save(any(AlertDomain.class))).thenReturn(savedAlert);
        
        // When
        AlertDomain result = service.createAlert(deviceId, deviceName, AlertType.DEVICE_DOWN, message,
                                               ipAddress, consecutiveFailures, failureReason);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeviceId()).isEqualTo(deviceId);
        assertThat(result.getIpAddress()).isEqualTo(ipAddress);
        assertThat(result.getConsecutiveFailures()).isEqualTo(consecutiveFailures);
        assertThat(result.getFailureReason()).isEqualTo(failureReason);
        
        verify(repository).findUnresolvedByDeviceId(deviceId);
        verify(repository).save(any(AlertDomain.class));
    }
}