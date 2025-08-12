package io.thatworked.support.device.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread pool configuration properties for async operations.
 */
@Data
public class ThreadPoolProperties {
    
    /**
     * Event publisher thread pool
     */
    @NotNull(message = "Event publisher configuration is required")
    private PoolConfig eventPublisher = new PoolConfig();
    
    /**
     * Default thread pool for other async operations
     */
    @NotNull(message = "Default pool configuration is required") 
    private PoolConfig defaultPool = new PoolConfig();
    
    /**
     * Custom thread pools by name
     */
    private Map<String, PoolConfig> custom = new HashMap<>();
    
    @Data
    public static class PoolConfig {
        /**
         * Core pool size
         */
        @Min(value = 1, message = "Core size must be at least 1")
        private int coreSize = 2;
        
        /**
         * Maximum pool size
         */
        @Min(value = 1, message = "Max size must be at least 1")
        private int maxSize = 10;
        
        /**
         * Queue capacity
         */
        @Min(value = 0, message = "Queue capacity cannot be negative")
        private int queueCapacity = 500;
        
        /**
         * Thread name prefix
         */
        @NotBlank(message = "Thread name prefix is required")
        private String threadNamePrefix = "async-";
        
        /**
         * Keep alive time in seconds
         */
        @Min(value = 0, message = "Keep alive time cannot be negative")
        private int keepAliveSeconds = 60;
        
        /**
         * Await termination seconds on shutdown
         */
        @Min(value = 0, message = "Await termination seconds cannot be negative")
        private int awaitTerminationSeconds = 60;
        
        /**
         * Allow core thread timeout
         */
        private boolean allowCoreThreadTimeout = false;
        
        /**
         * Wait for tasks to complete on shutdown
         */
        private boolean waitForTasksToCompleteOnShutdown = true;
    }
}