package io.thatworked.support.ping.infrastructure.executor;

import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.domain.PingStatus;
import io.thatworked.support.ping.infrastructure.event.PingResultEvent;
import io.thatworked.support.ping.infrastructure.queue.PingTask;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingResultRepository;
import io.thatworked.support.ping.config.PingExecutorConfig;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Virtual Thread-based ping executor for high scalability.
 * Uses Java 21 virtual threads to handle thousands of concurrent pings efficiently.
 */
@Component
public class VirtualThreadPingExecutor {
    
    private final StructuredLogger logger;
    private final PingResultRepository pingResultRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PingExecutionDelegate pingExecutionDelegate;
    private final PingExecutorConfig config;
    
    @Value("${ping.executor.timeout-ms:1000}")
    private int timeoutMs;
    
    @Value("${ping.executor.batch-interval-ms:100}")
    private int batchIntervalMs;
    
    @Value("${ping.executor.max-concurrent-pings:1000}")
    private int maxConcurrentPings;
    
    public VirtualThreadPingExecutor(StructuredLoggerFactory structuredLoggerFactory,
                                    PingResultRepository pingResultRepository,
                                    ApplicationEventPublisher eventPublisher,
                                    @Lazy PingExecutionDelegate pingExecutionDelegate,
                                    PingExecutorConfig config) {
        this.logger = structuredLoggerFactory.getLogger(VirtualThreadPingExecutor.class);
        this.pingResultRepository = pingResultRepository;
        this.eventPublisher = eventPublisher;
        this.pingExecutionDelegate = pingExecutionDelegate;
        this.config = config;
    }
    
    private ScheduledExecutorService scheduler;
    private ExecutorService virtualThreadExecutor;
    private final Map<String, ScheduledPingTask> scheduledTasks = new ConcurrentHashMap<>();
    private Semaphore concurrencyLimiter; // Will be initialized in @PostConstruct
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicInteger activePings = new AtomicInteger(0);
    
    // Metrics
    private final AtomicInteger totalPingsExecuted = new AtomicInteger(0);
    private final AtomicInteger successfulPings = new AtomicInteger(0);
    private final AtomicInteger failedPings = new AtomicInteger(0);
    
    @PostConstruct
    public void start() {
        try {
            logger.with("maxConcurrentPings", maxConcurrentPings).info("Starting Virtual Thread Ping Executor");
            
            // Initialize concurrency limiter with configured value
            concurrencyLimiter = new Semaphore(maxConcurrentPings);
            
            // Create virtual thread executor
            virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
            
            // Create scheduler for batch processing with configurable pool size
            scheduler = Executors.newScheduledThreadPool(config.getSchedulerPoolSize(), r -> {
                Thread t = new Thread(r);
                t.setName("ping-scheduler");
                t.setDaemon(true);
                return t;
            });
            
            running.set(true);
            
            logger.with("schedulerPoolSize", config.getSchedulerPoolSize())
                  .with("maxConcurrentPings", maxConcurrentPings)
                  .with("timeoutMs", timeoutMs)
                  .info("Virtual thread ping executor initialized");
            
            // Schedule metrics logging
            scheduler.scheduleAtFixedRate(this::logMetrics, 30, 30, TimeUnit.SECONDS);
            
            logger.with("status", "started").info("Virtual Thread Ping Executor started");
        } catch (Exception e) {
            logger.with("error", e.getMessage()).error("Failed to start Virtual Thread Ping Executor", e);
            throw new RuntimeException("Failed to start ping executor", e);
        }
    }
    
    @PreDestroy
    public void stop() {
        try {
            logger.with("status", "stopping").info("Stopping Virtual Thread Ping Executor");
            running.set(false);
            
            // Cancel all scheduled tasks
            try {
                scheduledTasks.values().forEach(task -> {
                    try {
                        task.future.cancel(false);
                    } catch (Exception e) {
                        logger.with("error", e.getMessage()).error("Error cancelling task", e);
                    }
                });
                scheduledTasks.clear();
            } catch (Exception e) {
                logger.with("error", e.getMessage()).error("Error cancelling scheduled tasks", e);
            }
            
            // Shutdown executors
            if (scheduler != null) {
                scheduler.shutdown();
            }
            if (virtualThreadExecutor != null) {
                virtualThreadExecutor.shutdown();
            }
            
            try {
                if (scheduler != null && !scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                if (virtualThreadExecutor != null && !virtualThreadExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    virtualThreadExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (scheduler != null) scheduler.shutdownNow();
                if (virtualThreadExecutor != null) virtualThreadExecutor.shutdownNow();
            }
            
            logger.with("status", "stopped").info("Virtual Thread Ping Executor stopped");
        } catch (Exception e) {
            logger.with("error", e.getMessage()).error("Error during Virtual Thread Ping Executor shutdown", e);
        }
    }
    
    /**
     * Schedule a ping task with the specified interval
     */
    public void schedulePing(PingTask task, Duration interval) {
        try {
            if (task == null || interval == null) {
                logger.with("method", "schedulePing")
                      .with("task", task)
                      .with("interval", interval)
                      .warn("Cannot schedule ping with null task or interval");
                return;
            }
            
            if (!running.get()) {
                logger.with("method", "schedulePing")
                      .with("running", running.get())
                      .warn("Cannot schedule ping - executor is not running");
                return;
            }
            
            String key = task.getDeviceId().toString();
            
            // Cancel existing task if present
            cancelPing(task.getDeviceId());
            
            // Schedule new task
            ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(
                () -> executePingAsync(task),
                0,
                interval.toMillis(),
                TimeUnit.MILLISECONDS
            );
            
            scheduledTasks.put(key, new ScheduledPingTask(task, future));
            logger.with("deviceId", key).with("intervalMs", interval.toMillis()).debug("Scheduled ping for device with interval");
        } catch (Exception e) {
            logger.with("deviceId", task != null ? task.getDeviceId() : "null").error("Error scheduling ping for device", e);
        }
    }
    
    /**
     * Cancel a scheduled ping
     */
    public void cancelPing(UUID deviceId) {
        try {
            if (deviceId == null) {
                logger.with("method", "cancelPing")
                      .with("deviceId", deviceId)
                      .warn("Cannot cancel ping with null deviceId");
                return;
            }
            
            String key = deviceId.toString();
            ScheduledPingTask scheduledTask = scheduledTasks.remove(key);
            
            if (scheduledTask != null) {
                try {
                    scheduledTask.future.cancel(false);
                    logger.with("deviceId", key).debug("Cancelled ping for device");
                } catch (Exception e) {
                    logger.with("deviceId", key).error("Error cancelling future for device", e);
                }
            }
        } catch (Exception e) {
            logger.with("deviceId", deviceId).error("Error cancelling ping for device", e);
        }
    }
    
    /**
     * Execute ping asynchronously using virtual thread
     */
    private void executePingAsync(PingTask task) {
        try {
            if (!running.get()) {
                return;
            }
            
            // Try to acquire permit (non-blocking)
            if (!concurrencyLimiter.tryAcquire()) {
                logger.with("deviceId", task.getDeviceId()).warn("Max concurrent pings reached, skipping ping for device");
                recordSkippedPing(task);
                return;
            }
            
            // Submit to virtual thread executor
            virtualThreadExecutor.submit(() -> {
                try {
                    activePings.incrementAndGet();
                    PingResult result = executePing(task);
                    processPingResult(result);
                    totalPingsExecuted.incrementAndGet();
                    if (result.getStatus().isSuccess()) {
                        successfulPings.incrementAndGet();
                    } else {
                        failedPings.incrementAndGet();
                    }
                } catch (Exception e) {
                    logger.with("deviceId", task.getDeviceId()).error("Error executing ping for device", e);
                    failedPings.incrementAndGet();
                    recordFailedPing(task, e);
                } finally {
                    activePings.decrementAndGet();
                    concurrencyLimiter.release();
                }
            });
        } catch (Exception e) {
            logger.with("deviceId", task != null ? task.getDeviceId() : "null").error("Error submitting ping task for device", e);
        }
    }
    
    /**
     * Execute the actual ping operation
     */
    private PingResult executePing(PingTask task) throws IOException {
        try {
            // Delegate to the ping service for execution
            if (pingExecutionDelegate != null) {
                return pingExecutionDelegate.executePing(task);
            } else {
                logger.with("component", "PingExecutionDelegate").error("PingExecutionDelegate not available", null);
                return PingResult.builder()
                        .time(Instant.now())
                        .deviceId(task.getDeviceId())
                        .status(PingStatus.ERROR)
                        .build();
            }
        } catch (Exception e) {
            logger.with("deviceId", task.getDeviceId()).with("error", e.getMessage()).debug("Error executing ping for device");
            if (e instanceof IOException) {
                throw (IOException) e;
            }
            throw new IOException("Ping execution failed", e);
        }
    }
    
    /**
     * Process and save ping result
     */
    private void processPingResult(PingResult result) {
        try {
            pingResultRepository.save(result);
            eventPublisher.publishEvent(new PingResultEvent(result));
        } catch (Exception e) {
            logger.with("deviceId", result.getDeviceId()).error("Failed to process ping result for device", e);
        }
    }
    
    /**
     * Record a skipped ping due to overload
     */
    private void recordSkippedPing(PingTask task) {
        PingResult result = PingResult.builder()
                .time(Instant.now())
                .deviceId(task.getDeviceId())
                .status(PingStatus.SKIPPED)
                .build();
        
        processPingResult(result);
    }
    
    /**
     * Record a failed ping
     */
    private void recordFailedPing(PingTask task, Exception error) {
        PingResult result = PingResult.builder()
                .time(Instant.now())
                .deviceId(task.getDeviceId())
                .status(PingStatus.ERROR)
                .build();
        
        processPingResult(result);
    }
    
    /**
     * Log executor metrics
     */
    private void logMetrics() {
        logger.with("activePings", activePings.get())
              .with("totalPingsExecuted", totalPingsExecuted.get())
              .with("successfulPings", successfulPings.get())
              .with("failedPings", failedPings.get())
              .with("successRate", totalPingsExecuted.get() > 0 ? 
                  (successfulPings.get() * 100.0 / totalPingsExecuted.get()) : 0)
              .info("Ping Executor Metrics");
    }
    
    /**
     * Get current metrics
     */
    public Map<String, Object> getMetrics() {
        return Map.of(
            "activePings", activePings.get(),
            "totalPingsExecuted", totalPingsExecuted.get(),
            "successfulPings", successfulPings.get(),
            "failedPings", failedPings.get(),
            "scheduledTasks", scheduledTasks.size(),
            "maxConcurrentPings", maxConcurrentPings
        );
    }
    
    /**
     * Container for scheduled ping task
     */
    private record ScheduledPingTask(PingTask task, ScheduledFuture<?> future) {}
}