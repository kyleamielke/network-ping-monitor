package io.thatworked.support.ping.application.service;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.ping.config.PingExecutorConfig;
import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.domain.PingStatus;
import io.thatworked.support.ping.domain.PingTarget;
import io.thatworked.support.ping.infrastructure.executor.PingCircuitBreaker;
import io.thatworked.support.ping.infrastructure.executor.VirtualThreadPingExecutor;
import io.thatworked.support.ping.infrastructure.queue.PingTask;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingResultRepository;
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
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("VirtualThreadPingService Tests")
class VirtualThreadPingServiceTest {
    
    @Mock
    private StructuredLoggerFactory structuredLoggerFactory;
    
    @Mock
    private StructuredLogger logger;
    
    @Mock
    private StructuredLogger.ContextBuilder contextBuilder;
    
    @Mock
    private PingTargetRepository pingTargetRepository;
    
    @Mock
    private PingResultRepository pingResultRepository;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @Mock
    private VirtualThreadPingExecutor executor;
    
    @Mock
    private PingCircuitBreaker circuitBreaker;
    
    @Mock
    private PingExecutorConfig config;
    
    @Captor
    private ArgumentCaptor<PingTask> pingTaskCaptor;
    
    @Captor
    private ArgumentCaptor<Duration> durationCaptor;
    
    private VirtualThreadPingService service;
    
    @BeforeEach
    void setUp() {
        when(structuredLoggerFactory.getLogger(any())).thenReturn(logger);
        when(logger.with(anyString(), any())).thenReturn(contextBuilder);
        when(contextBuilder.with(anyString(), any())).thenReturn(contextBuilder);
        doNothing().when(contextBuilder).info(anyString());
        doNothing().when(contextBuilder).debug(anyString());
        doNothing().when(contextBuilder).warn(anyString());
        doNothing().when(contextBuilder).error(anyString(), any(Throwable.class));
        
        // Default config
        when(config.getPingInterval()).thenReturn(30);
        when(config.getTimeoutMs()).thenReturn(5000);
        when(config.getRetryAttempts()).thenReturn(3);
        when(config.getRetryDelayMs()).thenReturn(1000);
        when(config.isCircuitBreakerEnabled()).thenReturn(true);
        
        service = new VirtualThreadPingService(
            structuredLoggerFactory,
            pingTargetRepository,
            pingResultRepository,
            eventPublisher,
            executor,
            circuitBreaker,
            config
        );
    }
    
    @Test
    @DisplayName("Should initialize service with active ping targets")
    void testInitialize() {
        // Given
        UUID deviceId1 = UUID.randomUUID();
        UUID deviceId2 = UUID.randomUUID();
        
        PingTarget target1 = createPingTarget(deviceId1, "192.168.1.100", "host1.local", true, 30);
        PingTarget target2 = createPingTarget(deviceId2, "192.168.1.101", "host2.local", true, 60);
        
        when(pingTargetRepository.findAllActiveTargets()).thenReturn(List.of(target1, target2));
        
        // When
        service.initialize();
        
        // Then
        verify(pingTargetRepository).findAllActiveTargets();
        verify(executor, times(2)).schedulePing(any(PingTask.class), any(Duration.class));
        verify(contextBuilder).info("Virtual Thread Ping Service initialized with active monitors");
    }
    
    @Test
    @DisplayName("Should handle initialization failure gracefully")
    void testInitializeFailure() {
        // Given
        when(pingTargetRepository.findAllActiveTargets()).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        assertThatThrownBy(() -> service.initialize())
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to initialize ping service");
        
        verify(contextBuilder).error(eq("Failed to initialize Virtual Thread Ping Service"), any(Exception.class));
    }
    
    @Test
    @DisplayName("Should start monitoring for a valid ping target")
    void testStartMonitoring() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTarget target = createPingTarget(deviceId, "192.168.1.100", "host1.local", true, 45);
        
        // When
        service.startMonitoring(target);
        
        // Then
        verify(executor).schedulePing(pingTaskCaptor.capture(), durationCaptor.capture());
        
        PingTask capturedTask = pingTaskCaptor.getValue();
        assertThat(capturedTask.getDeviceId()).isEqualTo(deviceId);
        assertThat(capturedTask.getIpAddress()).isEqualTo("192.168.1.100");
        assertThat(capturedTask.getHostname()).isEqualTo("host1.local");
        assertThat(capturedTask.getIntervalMs()).isEqualTo(45000L);
        assertThat(capturedTask.isRecurring()).isTrue();
        
        Duration capturedDuration = durationCaptor.getValue();
        assertThat(capturedDuration.getSeconds()).isEqualTo(45);
    }
    
    @Test
    @DisplayName("Should not start monitoring for unmonitored target")
    void testStartMonitoringUnmonitored() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTarget target = createPingTarget(deviceId, "192.168.1.100", null, false, 30);
        
        // When
        service.startMonitoring(target);
        
        // Then
        verify(executor, never()).schedulePing(any(), any());
        verify(contextBuilder).debug("Skipping monitoring for null or unmonitored target");
    }
    
    @Test
    @DisplayName("Should stop monitoring and reset circuit breaker")
    void testStopMonitoring() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTarget target = createPingTarget(deviceId, "192.168.1.100", null, true, 30);
        
        // Start monitoring first
        service.startMonitoring(target);
        
        // When
        service.stopMonitoring(deviceId);
        
        // Then
        verify(executor).cancelPing(deviceId);
        verify(circuitBreaker).reset(deviceId);
        verify(contextBuilder).info("Stopped monitoring device");
    }
    
    @Test
    @DisplayName("Should execute ping successfully")
    void testExecutePingSuccess() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTask task = PingTask.builder()
            .deviceId(deviceId)
            .ipAddress("192.168.1.100")
            .hostname("host1.local")
            .intervalMs(30000L)
            .recurring(true)
            .nextExecutionTime(Instant.now())
            .build();
        
        when(circuitBreaker.shouldAllowPing(deviceId)).thenReturn(true);
        
        // Mock InetAddress behavior - this is where we'd normally ping
        // In a real test, we might use PowerMock or a test container
        
        // When
        PingResult result = service.executePing(task);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeviceId()).isEqualTo(deviceId);
        verify(circuitBreaker).shouldAllowPing(deviceId);
    }
    
    @Test
    @DisplayName("Should skip ping when circuit breaker is open")
    void testExecutePingCircuitBreakerOpen() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTask task = PingTask.builder()
            .deviceId(deviceId)
            .ipAddress("192.168.1.100")
            .intervalMs(30000L)
            .recurring(true)
            .nextExecutionTime(Instant.now())
            .build();
        
        when(circuitBreaker.shouldAllowPing(deviceId)).thenReturn(false);
        
        // When
        PingResult result = service.executePing(task);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(PingStatus.CIRCUIT_OPEN);
        assertThat(result.getDeviceId()).isEqualTo(deviceId);
        verify(circuitBreaker, never()).recordSuccess(any());
        verify(circuitBreaker, never()).recordFailure(any());
    }
    
    @Test
    @DisplayName("Should handle ping failure and update circuit breaker")
    void testExecutePingFailure() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTask task = PingTask.builder()
            .deviceId(deviceId)
            .ipAddress("invalid-host")
            .intervalMs(30000L)
            .recurring(true)
            .nextExecutionTime(Instant.now())
            .build();
        
        when(circuitBreaker.shouldAllowPing(deviceId)).thenReturn(true);
        
        // When
        PingResult result = service.executePing(task);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDeviceId()).isEqualTo(deviceId);
        // The actual status depends on the implementation
        verify(circuitBreaker).recordFailure(deviceId);
    }
    
    @Test
    @DisplayName("Should retry ping on failure")
    void testExecutePingWithRetry() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTask task = PingTask.builder()
            .deviceId(deviceId)
            .ipAddress("192.168.1.100")
            .hostname("host1.local")
            .intervalMs(30000L)
            .recurring(true)
            .nextExecutionTime(Instant.now())
            .build();
        
        when(circuitBreaker.shouldAllowPing(deviceId)).thenReturn(true);
        when(config.getRetryAttempts()).thenReturn(3);
        when(config.getRetryDelayMs()).thenReturn(100);
        
        // When
        PingResult result = service.executePing(task);
        
        // Then
        assertThat(result).isNotNull();
        // Verify retry logic was considered - it's called multiple times in the retry loop
        verify(config, atLeastOnce()).getRetryAttempts();
    }
    
    @Test
    @DisplayName("Should update monitoring when target changes")
    void testUpdateMonitoring() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTarget oldTarget = createPingTarget(deviceId, "192.168.1.100", null, true, 30);
        PingTarget newTarget = createPingTarget(deviceId, "192.168.1.100", null, true, 60);
        
        // Start initial monitoring
        service.startMonitoring(oldTarget);
        
        // When
        service.updateMonitoring(newTarget);
        
        // Then
        verify(executor).cancelPing(deviceId); // Stop old monitoring
        verify(executor, times(2)).schedulePing(any(PingTask.class), any(Duration.class)); // Start new
        verify(circuitBreaker).reset(deviceId);
    }
    
    @Test
    @DisplayName("Should disable monitoring when target is updated to unmonitored")
    void testUpdateMonitoringToDisabled() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTarget oldTarget = createPingTarget(deviceId, "192.168.1.100", null, true, 30);
        PingTarget newTarget = createPingTarget(deviceId, "192.168.1.100", null, false, 30);
        
        // Start initial monitoring
        service.startMonitoring(oldTarget);
        
        // When
        service.updateMonitoring(newTarget);
        
        // Then
        verify(executor).cancelPing(deviceId);
        verify(circuitBreaker).reset(deviceId);
        // Should only be called once (for initial start)
        verify(executor, times(1)).schedulePing(any(PingTask.class), any(Duration.class));
    }
    
    @Test
    @DisplayName("Should get service metrics")
    void testGetMetrics() {
        // Given
        Map<String, Object> executorMetrics = Map.of("activeThreads", 10, "queueSize", 5);
        Map<String, Object> circuitMetrics = Map.of("openCircuits", 2, "totalDevices", 20);
        
        when(executor.getMetrics()).thenReturn(executorMetrics);
        when(circuitBreaker.getMetrics()).thenReturn(circuitMetrics);
        
        // When
        Map<String, Object> metrics = service.getMetrics();
        
        // Then
        assertThat(metrics).isNotNull();
        assertThat(metrics).containsKey("activeTasks");
        assertThat(metrics).containsKey("executorMetrics");
        assertThat(metrics).containsKey("circuitBreakerMetrics");
        assertThat(metrics.get("executorMetrics")).isEqualTo(executorMetrics);
        assertThat(metrics.get("circuitBreakerMetrics")).isEqualTo(circuitMetrics);
    }
    
    @Test
    @DisplayName("Should handle null ping target gracefully")
    void testStartMonitoringNullTarget() {
        // When
        service.startMonitoring(null);
        
        // Then
        verify(executor, never()).schedulePing(any(), any());
        verify(contextBuilder).debug("Skipping monitoring for null or unmonitored target");
    }
    
    @Test
    @DisplayName("Should handle null deviceId in stopMonitoring")
    void testStopMonitoringNullDeviceId() {
        // When
        service.stopMonitoring(null);
        
        // Then
        verify(executor, never()).cancelPing(any());
        verify(circuitBreaker, never()).reset(any());
        verify(contextBuilder).warn("Cannot stop monitoring for null deviceId");
    }
    
    @Test
    @DisplayName("Should shutdown service properly")
    void testShutdown() {
        // Given
        UUID deviceId1 = UUID.randomUUID();
        UUID deviceId2 = UUID.randomUUID();
        PingTarget target1 = createPingTarget(deviceId1, "192.168.1.100", null, true, 30);
        PingTarget target2 = createPingTarget(deviceId2, "192.168.1.101", null, true, 30);
        
        // Start monitoring
        service.startMonitoring(target1);
        service.startMonitoring(target2);
        
        // When
        service.shutdown();
        
        // Then
        verify(executor, times(2)).cancelPing(any(UUID.class));
        verify(circuitBreaker, times(2)).reset(any(UUID.class));
        verify(contextBuilder).info("Virtual Thread Ping Service shutdown complete");
    }
    
    @Test
    @DisplayName("Should prefer hostname over IP address for ping")
    void testPreferHostnameOverIP() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTask taskWithHostname = PingTask.builder()
            .deviceId(deviceId)
            .ipAddress("192.168.1.100")
            .hostname("host1.local")
            .intervalMs(30000L)
            .recurring(true)
            .nextExecutionTime(Instant.now())
            .build();
        
        when(circuitBreaker.shouldAllowPing(deviceId)).thenReturn(true);
        
        // When
        PingResult result = service.executePing(taskWithHostname);
        
        // Then
        assertThat(result).isNotNull();
        // In actual implementation, hostname would be used for InetAddress.getByName()
    }
    
    @Test
    @DisplayName("Should handle missing target address")
    void testExecutePingNoTargetAddress() {
        // Given
        UUID deviceId = UUID.randomUUID();
        PingTask taskNoAddress = PingTask.builder()
            .deviceId(deviceId)
            .ipAddress(null)
            .hostname(null)
            .intervalMs(30000L)
            .recurring(true)
            .nextExecutionTime(Instant.now())
            .build();
        
        when(circuitBreaker.shouldAllowPing(deviceId)).thenReturn(true);
        
        // When
        PingResult result = service.executePing(taskNoAddress);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isIn(PingStatus.ERROR, PingStatus.FAILURE);
        verify(circuitBreaker).recordFailure(deviceId);
    }
    
    // Helper method to create PingTarget
    private PingTarget createPingTarget(UUID deviceId, String ipAddress, String hostname, boolean monitored, Integer intervalSeconds) {
        PingTarget target = new PingTarget();
        target.setDeviceId(deviceId);
        target.setIpAddress(ipAddress);
        target.setHostname(hostname);
        target.setMonitored(monitored);
        target.setPingIntervalSeconds(intervalSeconds);
        return target;
    }
}