package io.thatworked.support.ping.infrastructure.executor;

import io.thatworked.support.ping.infrastructure.queue.PingTask;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Groups pings into time-based batches for efficient processing.
 * This reduces scheduling overhead and improves throughput.
 */
@Component
public class BatchPingProcessor {
    
    private final StructuredLogger logger;
    private final Map<Long, Queue<PingTask>> timeslotBuckets = new ConcurrentHashMap<>();
    private final ReentrantLock bucketLock = new ReentrantLock();
    private final long bucketSizeMs = 100; // 100ms buckets
    
    public BatchPingProcessor(StructuredLoggerFactory structuredLoggerFactory) {
        this.logger = structuredLoggerFactory.getLogger(BatchPingProcessor.class);
    }
    
    /**
     * Add a ping task to the appropriate time bucket
     */
    public void addPingTask(PingTask task, Instant executionTime) {
        try {
            if (task == null || executionTime == null) {
                logger.with("method", "addTask")
                      .with("task", task)
                      .with("executionTime", executionTime)
                      .warn("Cannot add null task or execution time");
                return;
            }
            
            long bucket = getBucketForTime(executionTime);
            
            timeslotBuckets.computeIfAbsent(bucket, k -> new ConcurrentLinkedQueue<>())
                          .offer(task);
            
            logger.with("deviceId", task.getDeviceId()).with("bucket", bucket).debug("Added ping task for device to bucket");
        } catch (Exception e) {
            logger.with("deviceId", task != null ? task.getDeviceId() : "null").error("Error adding ping task for device", e);
        }
    }
    
    /**
     * Get all tasks due for execution
     */
    public List<PingTask> getTasksDue(Instant now) {
        try {
            if (now == null) {
                logger.with("method", "getTasksDue")
                      .with("now", now)
                      .warn("Cannot get tasks due with null instant");
                return new ArrayList<>();
            }
            
            List<PingTask> dueTasks = new ArrayList<>();
            long currentBucket = getBucketForTime(now);
            
            // Get all buckets that are due (including past buckets)
            bucketLock.lock();
            try {
                Iterator<Map.Entry<Long, Queue<PingTask>>> iterator = timeslotBuckets.entrySet().iterator();
                
                while (iterator.hasNext()) {
                    Map.Entry<Long, Queue<PingTask>> entry = iterator.next();
                    
                    if (entry.getKey() <= currentBucket) {
                        Queue<PingTask> tasks = entry.getValue();
                        if (tasks != null) {
                            dueTasks.addAll(tasks);
                        }
                        iterator.remove();
                    }
                }
            } finally {
                bucketLock.unlock();
            }
            
            if (!dueTasks.isEmpty()) {
                logger.with("taskCount", dueTasks.size()).debug("Retrieved tasks due for execution");
            }
            
            return dueTasks;
        } catch (Exception e) {
            logger.with("error", e.getMessage()).error("Error getting tasks due for execution", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * Get tasks scheduled within the next duration
     */
    public List<PingTask> getUpcomingTasks(Duration lookahead) {
        Instant now = Instant.now();
        Instant future = now.plus(lookahead);
        
        long currentBucket = getBucketForTime(now);
        long futureBucket = getBucketForTime(future);
        
        List<PingTask> upcomingTasks = new ArrayList<>();
        
        timeslotBuckets.entrySet().stream()
            .filter(entry -> entry.getKey() > currentBucket && entry.getKey() <= futureBucket)
            .forEach(entry -> upcomingTasks.addAll(entry.getValue()));
        
        return upcomingTasks;
    }
    
    /**
     * Clear all scheduled tasks
     */
    public void clear() {
        try {
            bucketLock.lock();
            try {
                timeslotBuckets.clear();
                logger.with("action", "clear").info("Cleared all scheduled ping tasks");
            } finally {
                bucketLock.unlock();
            }
        } catch (Exception e) {
            logger.with("error", e.getMessage()).error("Error clearing scheduled tasks", e);
        }
    }
    
    /**
     * Get metrics about the batch processor
     */
    public Map<String, Object> getMetrics() {
        int totalTasks = timeslotBuckets.values().stream()
            .mapToInt(Queue::size)
            .sum();
        
        return Map.of(
            "buckets", timeslotBuckets.size(),
            "totalTasks", totalTasks,
            "bucketSizeMs", bucketSizeMs
        );
    }
    
    /**
     * Calculate which time bucket a given instant belongs to
     */
    private long getBucketForTime(Instant time) {
        return time.toEpochMilli() / bucketSizeMs;
    }
    
    /**
     * Clean up old empty buckets
     */
    public void cleanupOldBuckets() {
        long currentBucket = getBucketForTime(Instant.now());
        long cutoffBucket = currentBucket - 10; // Keep last 10 buckets (1 second)
        
        bucketLock.lock();
        try {
            timeslotBuckets.entrySet().removeIf(entry -> 
                entry.getKey() < cutoffBucket && entry.getValue().isEmpty()
            );
        } finally {
            bucketLock.unlock();
        }
    }
}