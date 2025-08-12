package io.thatworked.support.ping.application.service;

import io.thatworked.support.ping.config.PingExecutorConfig;
import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.domain.PingStatus;
import io.thatworked.support.ping.domain.PingTarget;
import io.thatworked.support.ping.infrastructure.event.PingResultEvent;
import io.thatworked.support.ping.infrastructure.executor.PingCircuitBreaker;
import io.thatworked.support.ping.infrastructure.executor.PingExecutionDelegate;
import io.thatworked.support.ping.infrastructure.executor.VirtualThreadPingExecutor;
import io.thatworked.support.ping.infrastructure.queue.PingTask;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingResultRepository;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingTargetRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * New ping service using virtual threads for scalability.
 * Replaces the old PingQueueManager with a more efficient implementation.
 */
@Service
@Primary
public class VirtualThreadPingService implements PingExecutionDelegate {
    
    private final StructuredLogger logger;
    private final PingTargetRepository pingTargetRepository;
    private final PingResultRepository pingResultRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final VirtualThreadPingExecutor executor;
    private final PingCircuitBreaker circuitBreaker;
    private final PingExecutorConfig config;
    
    public VirtualThreadPingService(StructuredLoggerFactory structuredLoggerFactory,
                                  PingTargetRepository pingTargetRepository,
                                  PingResultRepository pingResultRepository,
                                  ApplicationEventPublisher eventPublisher,
                                  VirtualThreadPingExecutor executor,
                                  PingCircuitBreaker circuitBreaker,
                                  PingExecutorConfig config) {
        this.logger = structuredLoggerFactory.getLogger(VirtualThreadPingService.class);
        this.pingTargetRepository = pingTargetRepository;
        this.pingResultRepository = pingResultRepository;
        this.eventPublisher = eventPublisher;
        this.executor = executor;
        this.circuitBreaker = circuitBreaker;
        this.config = config;
    }
    
    private final Map<UUID, PingTask> activeTasks = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void initialize() {
        try {
            logger.with("method", "initialize")
                  .info("Initializing Virtual Thread Ping Service");
            
            // Load all active ping targets
            List<PingTarget> activeTargets = pingTargetRepository.findAllActiveTargets();
            logger.with("activeTargets", activeTargets.size()).info("Found active ping targets to monitor");
            
            // Schedule each target
            for (PingTarget target : activeTargets) {
                try {
                    startMonitoring(target);
                } catch (Exception e) {
                    logger.with("deviceId", target.getDeviceId()).error("Failed to start monitoring for device", e);
                }
            }
            
            logger.with("activeMonitors", activeTasks.size()).info("Virtual Thread Ping Service initialized with active monitors");
        } catch (Exception e) {
            logger.with("method", "initialize")
                  .error("Failed to initialize Virtual Thread Ping Service", e);
            throw new RuntimeException("Failed to initialize ping service", e);
        }
    }
    
    @PreDestroy
    public void shutdown() {
        try {
            logger.with("method", "shutdown")
                  .info("Shutting down Virtual Thread Ping Service");
            
            // Stop all monitoring
            activeTasks.keySet().forEach(this::stopMonitoring);
            
            logger.with("method", "shutdown")
                  .info("Virtual Thread Ping Service shutdown complete");
        } catch (Exception e) {
            logger.with("method", "shutdown")
                  .error("Error during Virtual Thread Ping Service shutdown", e);
        }
    }
    
    /**
     * Start monitoring a device
     */
    public void startMonitoring(PingTarget target) {
        try {
            if (target == null || !target.isMonitored()) {
                logger.with("method", "startMonitoring")
                      .debug("Skipping monitoring for null or unmonitored target");
                return;
            }
            
            UUID deviceId = target.getDeviceId();
            
            // Create ping task with circuit breaker check
            int intervalSeconds = target.getPingIntervalSeconds() != null ? 
                target.getPingIntervalSeconds() : config.getPingInterval();
            
            PingTask task = PingTask.builder()
                .deviceId(deviceId)
                .ipAddress(target.getIpAddress())
                .hostname(target.getHostname())
                .intervalMs(intervalSeconds * 1000L)
                .recurring(true)
                .nextExecutionTime(Instant.now())
                .build();
            
            // Schedule with executor
            Duration interval = Duration.ofSeconds(intervalSeconds);
            
            // Use the task directly - circuit breaker check happens in executePing
            PingTask wrappedTask = task;
            
            executor.schedulePing(wrappedTask, interval);
            activeTasks.put(deviceId, wrappedTask);
            
            logger.with("deviceId", deviceId).with("intervalSeconds", interval.getSeconds()).info("Started monitoring device with interval");
        } catch (Exception e) {
            logger.with("deviceId", target != null ? target.getDeviceId() : "null").error("Error starting monitoring for device", e);
        }
    }
    
    /**
     * Stop monitoring a device
     */
    public void stopMonitoring(UUID deviceId) {
        try {
            if (deviceId == null) {
                logger.with("method", "stopMonitoring")
                      .warn("Cannot stop monitoring for null deviceId");
                return;
            }
            
            PingTask task = activeTasks.remove(deviceId);
            if (task != null) {
                executor.cancelPing(deviceId);
                circuitBreaker.reset(deviceId);
                logger.with("deviceId", deviceId).info("Stopped monitoring device");
            }
        } catch (Exception e) {
            logger.with("deviceId", deviceId).error("Error stopping monitoring for device", e);
        }
    }
    
    /**
     * Execute a ping with circuit breaker and retry logic
     */
    public PingResult executePing(PingTask task) {
        try {
            // Check circuit breaker
            if (config.isCircuitBreakerEnabled() && !circuitBreaker.shouldAllowPing(task.getDeviceId())) {
                logger.with("deviceId", task.getDeviceId()).debug("Circuit breaker OPEN for device, skipping ping");
                return PingResult.builder()
                    .time(Instant.now())
                    .deviceId(task.getDeviceId())
                    .status(PingStatus.CIRCUIT_OPEN)
                    .build();
            }
            
            // Execute ping with retries
            PingResult result = executeWithRetry(task);
            
            // Update circuit breaker
            if (config.isCircuitBreakerEnabled()) {
                if (result.getStatus().isSuccess()) {
                    circuitBreaker.recordSuccess(task.getDeviceId());
                } else {
                    circuitBreaker.recordFailure(task.getDeviceId());
                }
            }
            
            return result;
        } catch (Exception e) {
            logger.with("deviceId", task.getDeviceId()).error("Error executing ping for device", e);
            
            if (config.isCircuitBreakerEnabled()) {
                circuitBreaker.recordFailure(task.getDeviceId());
            }
            
            return PingResult.builder()
                .time(Instant.now())
                .deviceId(task.getDeviceId())
                .status(PingStatus.ERROR)
                .build();
        }
    }
    
    /**
     * Execute ping with retry logic
     */
    private PingResult executeWithRetry(PingTask task) throws IOException {
        IOException lastException = null;
        
        for (int attempt = 0; attempt < config.getRetryAttempts(); attempt++) {
            try {
                return doExecutePing(task);
            } catch (IOException e) {
                lastException = e;
                
                if (attempt < config.getRetryAttempts() - 1) {
                    try {
                        Thread.sleep(config.getRetryDelayMs() * (attempt + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrupted during retry delay", ie);
                    }
                }
            }
        }
        
        throw new IOException("All ping attempts failed", lastException);
    }
    
    /**
     * Execute the actual ping operation
     */
    private PingResult doExecutePing(PingTask task) throws IOException {
        Instant startTime = Instant.now();
        
        try {
            // Prefer hostname over IP address if available
            String target = (task.getHostname() != null && !task.getHostname().isEmpty()) 
                ? task.getHostname() 
                : task.getIpAddress();
                
            if (target == null || target.isEmpty()) {
                throw new IOException("No target address (IP or hostname) available for device: " + task.getDeviceId());
            }
            
            InetAddress address = InetAddress.getByName(target);
            long start = System.nanoTime();
            boolean reachable = address.isReachable(config.getTimeoutMs());
            long pingTimeNanos = System.nanoTime() - start;
            
            double rttMs = pingTimeNanos / 1_000_000.0;
            
            return PingResult.builder()
                .time(startTime)
                .deviceId(task.getDeviceId())
                .status(reachable ? PingStatus.SUCCESS : PingStatus.FAILURE)
                .roundTripTime(reachable ? rttMs : null)
                .build();
        } catch (IOException e) {
            logger.with("ipAddress", task.getIpAddress()).with("error", e.getMessage()).debug("Ping failed");
            throw e;
        }
    }
    
    /**
     * Get service metrics
     */
    public Map<String, Object> getMetrics() {
        try {
            Map<String, Object> metrics = new ConcurrentHashMap<>();
            
            metrics.put("activeTasks", activeTasks.size());
            metrics.put("executorMetrics", executor.getMetrics());
            
            if (config.isCircuitBreakerEnabled()) {
                metrics.put("circuitBreakerMetrics", circuitBreaker.getMetrics());
            }
            
            return metrics;
        } catch (Exception e) {
            logger.with("method", "getMetrics")
                  .error("Error getting metrics", e);
            return Map.of("error", "Failed to retrieve metrics");
        }
    }
    
    /**
     * Update monitoring for a ping target
     */
    public void updateMonitoring(PingTarget target) {
        try {
            if (target == null) {
                return;
            }
            
            UUID deviceId = target.getDeviceId();
            
            // Stop existing monitoring
            stopMonitoring(deviceId);
            
            // Start new monitoring if enabled
            if (target.isMonitored()) {
                startMonitoring(target);
            }
        } catch (Exception e) {
            logger.with("deviceId", target != null ? target.getDeviceId() : "null").error("Error updating monitoring for device", e);
        }
    }
}