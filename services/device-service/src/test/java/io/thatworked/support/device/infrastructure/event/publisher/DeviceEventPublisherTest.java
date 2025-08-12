package io.thatworked.support.device.infrastructure.event.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.device.config.properties.DeviceServiceProperties;
import io.thatworked.support.device.config.properties.KafkaProperties;
import io.thatworked.support.device.infrastructure.entity.Device;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DeviceEventPublisher Tests")
class DeviceEventPublisherTest {

    @Mock
    private StructuredLoggerFactory loggerFactory;
    
    @Mock
    private StructuredLogger logger;
    
    @Mock
    private StructuredLogger.ContextBuilder contextBuilder;
    
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private DeviceServiceProperties properties;
    
    @Mock
    private KafkaProperties kafkaProperties;
    
    @Mock
    private KafkaProperties.TopicProperties topicProperties;
    
    private DeviceEventPublisher publisher;
    
    @BeforeEach
    void setUp() {
        // Mock logger
        when(loggerFactory.getLogger(any(Class.class))).thenReturn(logger);
        when(logger.with(anyString(), any())).thenReturn(contextBuilder);
        when(contextBuilder.with(anyString(), any())).thenReturn(contextBuilder);
        doNothing().when(contextBuilder).debug(anyString());
        doNothing().when(contextBuilder).info(anyString());
        doNothing().when(contextBuilder).error(anyString(), any());
        
        // Mock properties
        when(properties.getKafka()).thenReturn(kafkaProperties);
        when(kafkaProperties.isEnabled()).thenReturn(true);
        when(kafkaProperties.getTopic()).thenReturn(topicProperties);
        when(topicProperties.getDeviceEvents()).thenReturn("device-events");
        
        publisher = new DeviceEventPublisher(loggerFactory, kafkaTemplate, objectMapper, properties);
    }
    
    @Test
    @DisplayName("Should publish device created event to Kafka")
    void testPublishDeviceCreated() {
        // Given
        Device device = new Device();
        device.setId(UUID.randomUUID());
        device.setName("test-device");
        device.setIpAddress("192.168.1.100");
        device.setStatus("ACTIVE");
        device.setCreatedAt(Instant.now());
        device.setUpdatedAt(Instant.now());
        
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
        
        // When
        publisher.publishDeviceCreated(device);
        
        // Then
        verify(kafkaTemplate).send(eq("device-events"), eq(device.getId().toString()), any(Map.class));
    }
    
    @Test
    @DisplayName("Should publish device updated event with changes to Kafka")
    void testPublishDeviceUpdated() {
        // Given
        Device device = new Device();
        device.setId(UUID.randomUUID());
        device.setName("updated-device");
        device.setIpAddress("192.168.1.200");
        device.setStatus("ACTIVE");
        device.setVersion(2L);
        device.setUpdatedAt(Instant.now());
        
        Map<String, Object> changes = new HashMap<>();
        changes.put("name", "updated-device");
        changes.put("ipAddress", "192.168.1.200");
        
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
        
        // When
        publisher.publishDeviceUpdated(device, changes);
        
        // Then
        verify(kafkaTemplate).send(eq("device-events"), eq(device.getId().toString()), any(Map.class));
    }
    
    @Test
    @DisplayName("Should publish device deleted event to Kafka")
    void testPublishDeviceDeleted() {
        // Given
        UUID deviceId = UUID.randomUUID();
        
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
        
        // When
        publisher.publishDeviceDeleted(deviceId);
        
        // Then
        verify(kafkaTemplate).send(eq("device-events"), eq(deviceId.toString()), any(Map.class));
    }
    
    @Test
    @DisplayName("Should handle Kafka send failure gracefully")
    void testHandleKafkaSendFailure() {
        // Given
        Device device = new Device();
        device.setId(UUID.randomUUID());
        device.setName("test-device");
        device.setIpAddress("192.168.1.100");
        device.setStatus("ACTIVE");
        
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
        
        // When - should not throw
        publisher.publishDeviceCreated(device);
        
        // Then - verify attempt was made
        verify(kafkaTemplate).send(eq("device-events"), eq(device.getId().toString()), any());
    }
}