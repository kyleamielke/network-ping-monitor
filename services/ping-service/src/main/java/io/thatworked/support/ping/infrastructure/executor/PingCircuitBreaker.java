package io.thatworked.support.ping.infrastructure.executor;

import lombok.Getter;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.ping.config.PingExecutorConfig;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Circuit breaker for ping operations to prevent wasting resources on consistently unreachable hosts.
 * Implements a half-open state to periodically retry failed hosts.
 */
@Component
public class PingCircuitBreaker {
    
    private final StructuredLogger logger;
    private final Map<UUID, CircuitState> circuits = new ConcurrentHashMap<>();
    
    // Configuration from PingExecutorConfig
    private final int failureThreshold;
    private final Duration openDuration;
    private final Duration halfOpenTestInterval;
    private final boolean circuitBreakerEnabled;
    
    public PingCircuitBreaker(StructuredLoggerFactory structuredLoggerFactory, PingExecutorConfig config) {
        this.logger = structuredLoggerFactory.getLogger(PingCircuitBreaker.class);
        this.failureThreshold = config.getCircuitBreakerFailureThreshold();
        this.openDuration = Duration.ofMinutes(config.getCircuitBreakerOpenDurationMinutes());
        this.halfOpenTestInterval = Duration.ofMinutes(config.getCircuitBreakerHalfOpenIntervalMinutes());
        this.circuitBreakerEnabled = config.isCircuitBreakerEnabled();
        
        logger.with("failureThreshold", failureThreshold)
              .with("openDurationMinutes", config.getCircuitBreakerOpenDurationMinutes())
              .with("halfOpenIntervalMinutes", config.getCircuitBreakerHalfOpenIntervalMinutes())
              .with("enabled", circuitBreakerEnabled)
              .info("Circuit breaker initialized with configuration");
    }
    
    public enum State {
        CLOSED,      // Normal operation
        OPEN,        // Failures exceeded threshold, blocking requests
        HALF_OPEN    // Testing if service recovered
    }
    
    /**
     * Check if ping should be allowed for the device
     */
    public boolean shouldAllowPing(UUID deviceId) {
        try {
            // If circuit breaker is disabled, always allow pings
            if (!circuitBreakerEnabled) {
                return true;
            }
            
            if (deviceId == null) {
                logger.with("method", "shouldAllowPing").warn("Null deviceId provided to circuit breaker");
                return true; // Allow ping by default for null devices
            }
            
            CircuitState state = circuits.computeIfAbsent(deviceId, k -> new CircuitState());
            
            switch (state.getCurrentState()) {
                case State.CLOSED -> {
                    return true;
                }
                case State.OPEN -> {
                    // Check if it's time to transition to half-open
                    if (state.shouldTransitionToHalfOpen()) {
                        state.transitionToHalfOpen();
                        logger.with("deviceId", deviceId).info("Circuit breaker for device transitioned to HALF_OPEN");
                        return true; // Allow one test ping
                    }
                    return false;
                }
                case State.HALF_OPEN -> {
                    // Only allow periodic test pings
                    return state.shouldAllowHalfOpenTest();
                }
                default -> {
                    logger.with("deviceId", deviceId).with("state", state.getState()).warn("Unknown circuit state for device");
                    return true;
                }
            }
        } catch (Exception e) {
            logger.with("deviceId", deviceId).error("Error checking circuit breaker for device", e);
            return true; // Default to allowing ping on error
        }
    }
    
    /**
     * Record successful ping
     */
    public void recordSuccess(UUID deviceId) {
        try {
            // If circuit breaker is disabled, do nothing
            if (!circuitBreakerEnabled) {
                return;
            }
            
            if (deviceId == null) {
                logger.with("method", "recordSuccess").warn("Null deviceId provided to recordSuccess");
                return;
            }
            
            CircuitState state = circuits.get(deviceId);
            if (state == null) {
                return;
            }
            
            switch (state.getCurrentState()) {
                case State.HALF_OPEN -> {
                    // Success in half-open state closes the circuit
                    state.reset();
                    logger.with("deviceId", deviceId).info("Circuit breaker for device closed after successful ping");
                }
                case State.OPEN -> {
                    // Shouldn't happen, but reset anyway
                    state.reset();
                }
                case State.CLOSED -> {
                    // Reset consecutive failures
                    state.resetFailures();
                }
                default -> {
                    logger.with("deviceId", deviceId).with("state", state.getState()).warn("Unknown circuit state on success for device");
                }
            }
        } catch (Exception e) {
            logger.with("deviceId", deviceId).error("Error recording success for device", e);
        }
    }
    
    /**
     * Record failed ping
     */
    public void recordFailure(UUID deviceId) {
        try {
            // If circuit breaker is disabled, do nothing
            if (!circuitBreakerEnabled) {
                return;
            }
            
            if (deviceId == null) {
                logger.with("method", "recordFailure").warn("Null deviceId provided to recordFailure");
                return;
            }
            
            CircuitState state = circuits.computeIfAbsent(deviceId, k -> new CircuitState());
            
            switch (state.getCurrentState()) {
                case State.CLOSED -> {
                    if (state.incrementFailures() >= failureThreshold) {
                        state.transitionToOpen();
                        logger.with("deviceId", deviceId).with("failureThreshold", failureThreshold).warn("Circuit breaker for device opened after failures");
                    }
                }
                case State.HALF_OPEN -> {
                    // Failure in half-open state reopens the circuit
                    state.transitionToOpen();
                    logger.with("deviceId", deviceId).info("Circuit breaker for device reopened after half-open test failure");
                }
                case State.OPEN -> {
                    // Already open, update last failure time
                    state.updateLastFailureTime();
                }
                default -> {
                    logger.with("deviceId", deviceId).with("state", state.getState()).warn("Unknown circuit state on failure for device");
                }
            }
        } catch (Exception e) {
            logger.with("deviceId", deviceId).error("Error recording failure for device", e);
        }
    }
    
    /**
     * Get circuit breaker metrics
     */
    public Map<String, Object> getMetrics() {
        try {
            int openCircuits = 0;
            int halfOpenCircuits = 0;
            int closedCircuits = 0;
            
            for (CircuitState state : circuits.values()) {
                try {
                    switch (state.getCurrentState()) {
                        case State.OPEN -> openCircuits++;
                        case State.HALF_OPEN -> halfOpenCircuits++;
                        case State.CLOSED -> closedCircuits++;
                    }
                } catch (Exception e) {
                    logger.with("method", "getMetrics").error("Error getting state for circuit", e);
                }
            }
            
            return Map.of(
                "totalCircuits", circuits.size(),
                "openCircuits", openCircuits,
                "halfOpenCircuits", halfOpenCircuits,
                "closedCircuits", closedCircuits
            );
        } catch (Exception e) {
            logger.with("method", "getMetrics").error("Error getting circuit breaker metrics", e);
            return Map.of(
                "error", "Failed to retrieve metrics",
                "totalCircuits", circuits.size()
            );
        }
    }
    
    /**
     * Reset circuit breaker for a device
     */
    public void reset(UUID deviceId) {
        try {
            if (deviceId == null) {
                logger.with("method", "reset").warn("Null deviceId provided to reset");
                return;
            }
            
            CircuitState state = circuits.get(deviceId);
            if (state != null) {
                state.reset();
                logger.with("deviceId", deviceId).info("Circuit breaker for device manually reset");
            }
        } catch (Exception e) {
            logger.with("deviceId", deviceId).error("Error resetting circuit breaker for device", e);
        }
    }
    
    /**
     * Clear all circuit breakers
     */
    public void clearAll() {
        try {
            circuits.clear();
            logger.with("method", "clearAll").info("All circuit breakers cleared");
        } catch (Exception e) {
            logger.with("method", "clearAll").error("Error clearing all circuit breakers", e);
        }
    }
    
    /**
     * Internal circuit state tracking
     */
    @Getter
    private class CircuitState {
        private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
        private final AtomicInteger consecutiveFailures = new AtomicInteger(0);
        private volatile Instant lastFailureTime;
        private volatile Instant lastStateChangeTime;
        private volatile Instant lastHalfOpenTest;
        
        public CircuitState() {
            this.lastStateChangeTime = Instant.now();
        }
        
        public State getCurrentState() {
            return state.get();
        }
        
        public boolean shouldTransitionToHalfOpen() {
            try {
                return state.get() == State.OPEN && 
                       Duration.between(lastStateChangeTime, Instant.now()).compareTo(openDuration) > 0;
            } catch (Exception e) {
                logger.with("method", "shouldTransitionToHalfOpen").error("Error checking half-open transition", e);
                return false;
            }
        }
        
        public boolean shouldAllowHalfOpenTest() {
            try {
                if (lastHalfOpenTest == null) {
                    lastHalfOpenTest = Instant.now();
                    return true;
                }
                
                if (Duration.between(lastHalfOpenTest, Instant.now()).compareTo(halfOpenTestInterval) > 0) {
                    lastHalfOpenTest = Instant.now();
                    return true;
                }
                
                return false;
            } catch (Exception e) {
                logger.with("method", "shouldAllowHalfOpenTest").error("Error checking half-open test allowance", e);
                return false;
            }
        }
        
        public int incrementFailures() {
            try {
                lastFailureTime = Instant.now();
                return consecutiveFailures.incrementAndGet();
            } catch (Exception e) {
                logger.with("method", "incrementFailures").error("Error incrementing failures", e);
                return consecutiveFailures.get();
            }
        }
        
        public void resetFailures() {
            try {
                consecutiveFailures.set(0);
            } catch (Exception e) {
                logger.with("method", "resetFailures").error("Error resetting failures", e);
            }
        }
        
        public void transitionToOpen() {
            try {
                state.set(State.OPEN);
                lastStateChangeTime = Instant.now();
                lastFailureTime = Instant.now();
            } catch (Exception e) {
                logger.with("method", "transitionToOpen").error("Error transitioning to OPEN state", e);
            }
        }
        
        public void transitionToHalfOpen() {
            try {
                state.set(State.HALF_OPEN);
                lastStateChangeTime = Instant.now();
                lastHalfOpenTest = null;
            } catch (Exception e) {
                logger.with("method", "transitionToHalfOpen").error("Error transitioning to HALF_OPEN state", e);
            }
        }
        
        public void reset() {
            try {
                state.set(State.CLOSED);
                consecutiveFailures.set(0);
                lastStateChangeTime = Instant.now();
                lastHalfOpenTest = null;
            } catch (Exception e) {
                logger.with("method", "reset").error("Error resetting circuit state", e);
            }
        }
        
        public void updateLastFailureTime() {
            try {
                lastFailureTime = Instant.now();
            } catch (Exception e) {
                logger.with("method", "updateLastFailureTime").error("Error updating last failure time", e);
            }
        }
    }
}