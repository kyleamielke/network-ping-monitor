package io.thatworked.support.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Circuit Breaker configuration for API Gateway.
 * Provides resilience patterns for downstream service calls.
 */
@Configuration
public class CircuitBreakerConfig {

    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        io.github.resilience4j.circuitbreaker.CircuitBreakerConfig config = 
            io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                // Failure rate threshold (50%)
                .failureRateThreshold(50.0f)
                // Minimum number of calls before calculating failure rate
                .minimumNumberOfCalls(5)
                // Wait duration in open state before transitioning to half-open
                .waitDurationInOpenState(Duration.ofSeconds(30))
                // Number of permitted calls in half-open state
                .permittedNumberOfCallsInHalfOpenState(3)
                // Sliding window size for failure rate calculation
                .slidingWindowSize(10)
                // Use count-based sliding window
                .slidingWindowType(SlidingWindowType.COUNT_BASED)
                // Record specific exceptions as failures
                .recordExceptions(
                    java.net.ConnectException.class,
                    java.net.SocketTimeoutException.class,
                    java.io.IOException.class,
                    feign.RetryableException.class
                )
                // Ignore certain exceptions
                .ignoreExceptions(
                    IllegalArgumentException.class,
                    jakarta.validation.ValidationException.class
                )
                .build();
        
        return CircuitBreakerRegistry.of(config);
    }

    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
                // Maximum retry attempts
                .maxAttempts(3)
                // Wait duration between retries
                .waitDuration(Duration.ofMillis(500))
                // Retry on specific exceptions
                .retryExceptions(
                    java.net.ConnectException.class,
                    java.net.SocketTimeoutException.class,
                    feign.RetryableException.class
                )
                // Don't retry on these exceptions
                .ignoreExceptions(
                    IllegalArgumentException.class,
                    jakarta.validation.ValidationException.class
                )
                .build();
        
        return RetryRegistry.of(config);
    }

    @Bean
    public TimeLimiterRegistry timeLimiterRegistry() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                // Timeout for async operations
                .timeoutDuration(Duration.ofSeconds(10))
                // Cancel running future on timeout
                .cancelRunningFuture(true)
                .build();
        
        return TimeLimiterRegistry.of(config);
    }
}