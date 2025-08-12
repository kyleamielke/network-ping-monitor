package io.thatworked.support.device.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Resilience configuration for circuit breakers, retries, and timeouts.
 */
@Data
public class ResilienceProperties {
    
    /**
     * Circuit breaker configuration
     */
    @NotNull(message = "Circuit breaker configuration is required")
    private CircuitBreakerProperties circuitBreaker = new CircuitBreakerProperties();
    
    /**
     * Retry configuration
     */
    @NotNull(message = "Retry configuration is required")
    private RetryProperties retry = new RetryProperties();
    
    /**
     * Timeout configuration
     */
    @NotNull(message = "Timeout configuration is required")
    private TimeoutProperties timeout = new TimeoutProperties();
    
    @Data
    public static class CircuitBreakerProperties {
        /**
         * Failure rate threshold percentage
         */
        @Min(value = 1, message = "Failure rate threshold must be at least 1")
        @Max(value = 100, message = "Failure rate threshold cannot exceed 100")
        private int failureRateThreshold = 50;
        
        /**
         * Minimum number of calls before calculating failure rate
         */
        @Min(value = 1, message = "Minimum calls must be at least 1")
        private int minimumNumberOfCalls = 10;
        
        /**
         * Wait duration in open state
         */
        @NotNull(message = "Wait duration is required")
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration waitDurationInOpenState = Duration.ofSeconds(30);
        
        /**
         * Sliding window size for call recording
         */
        @Min(value = 1, message = "Sliding window size must be at least 1")
        private int slidingWindowSize = 20;
        
        /**
         * Enable circuit breaker
         */
        private boolean enabled = true;
    }
    
    @Data
    public static class RetryProperties {
        /**
         * Maximum retry attempts
         */
        @Min(value = 0, message = "Max attempts cannot be negative")
        private int maxAttempts = 3;
        
        /**
         * Wait duration between retries
         */
        @NotNull(message = "Wait duration is required")
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration waitDuration = Duration.ofSeconds(1);
        
        /**
         * Exponential backoff multiplier
         */
        @Min(value = 1, message = "Backoff multiplier must be at least 1")
        private double backoffMultiplier = 2.0;
        
        /**
         * Enable retry
         */
        private boolean enabled = true;
    }
    
    @Data
    public static class TimeoutProperties {
        /**
         * Default timeout duration
         */
        @NotNull(message = "Default timeout is required")
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration defaultTimeout = Duration.ofSeconds(10);
        
        /**
         * Database query timeout
         */
        @NotNull(message = "Database timeout is required")
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration databaseTimeout = Duration.ofSeconds(5);
        
        /**
         * HTTP request timeout
         */
        @NotNull(message = "HTTP timeout is required")
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration httpTimeout = Duration.ofSeconds(30);
    }
}