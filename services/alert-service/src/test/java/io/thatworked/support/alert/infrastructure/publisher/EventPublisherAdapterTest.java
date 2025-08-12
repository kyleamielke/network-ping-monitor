package io.thatworked.support.alert.infrastructure.publisher;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.infrastructure.adapter.EventPublisherAdapter;
import io.thatworked.support.alert.infrastructure.event.publisher.AlertEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Alert EventPublisherAdapter Tests")
class EventPublisherAdapterTest {

    @Mock
    private AlertEventPublisher kafkaPublisher;
    
    private EventPublisherAdapter publisher;
    
    @BeforeEach
    void setUp() {
        publisher = new EventPublisherAdapter(kafkaPublisher);
    }
    
    @Test
    @DisplayName("Should delegate alert created event to Kafka publisher")
    void testPublishAlertCreated() {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        AlertDomain alert = new AlertDomain(
            alertId, deviceId, "test-device", AlertType.DEVICE_DOWN,
            "Device is down", Instant.now(), false, null,
            false, null, null,
            Instant.now(), Instant.now(), "192.168.1.100",
            3, "Ping timeout", 1L
        );
        
        // When
        publisher.publishAlertCreated(alert);
        
        // Then
        verify(kafkaPublisher).publishAlertCreated(alert);
    }
    
    @Test
    @DisplayName("Should delegate alert resolved event to Kafka publisher")
    void testPublishAlertResolved() {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        Instant resolvedAt = Instant.now();
        
        AlertDomain alert = new AlertDomain(
            alertId, deviceId, "test-device", AlertType.DEVICE_DOWN,
            "Device is down", Instant.now().minusSeconds(300), true, resolvedAt,
            false, null, null,
            Instant.now().minusSeconds(300), Instant.now(), "192.168.1.100",
            3, "Ping timeout", 1L
        );
        
        // When
        publisher.publishAlertResolved(alert);
        
        // Then
        verify(kafkaPublisher).publishAlertResolved(alert);
    }
    
    @Test
    @DisplayName("Should delegate alert acknowledged event to Kafka publisher")
    void testPublishAlertAcknowledged() {
        // Given
        UUID alertId = UUID.randomUUID();
        UUID deviceId = UUID.randomUUID();
        String acknowledgedBy = "admin@example.com";
        Instant acknowledgedAt = Instant.now();
        
        AlertDomain alert = new AlertDomain(
            alertId, deviceId, "test-device", AlertType.DEVICE_DOWN,
            "Device is down", Instant.now().minusSeconds(300), false, null,
            true, acknowledgedAt, acknowledgedBy,
            Instant.now().minusSeconds(300), Instant.now(), "192.168.1.100",
            3, "Ping timeout", 1L
        );
        
        // When
        publisher.publishAlertAcknowledged(alert);
        
        // Then
        verify(kafkaPublisher).publishAlertAcknowledged(alert);
    }
}