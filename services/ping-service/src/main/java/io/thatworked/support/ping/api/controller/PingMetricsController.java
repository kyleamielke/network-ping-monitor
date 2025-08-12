package io.thatworked.support.ping.api.controller;

import io.thatworked.support.ping.infrastructure.executor.PingCircuitBreaker;
import io.thatworked.support.ping.infrastructure.executor.VirtualThreadPingExecutor;
import io.thatworked.support.ping.application.service.VirtualThreadPingService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for ping service metrics and management
 */
@RestController
@RequestMapping("/api/v1/ping/metrics")
public class PingMetricsController {
    
    private final StructuredLogger logger;
    private final VirtualThreadPingService pingService;
    private final VirtualThreadPingExecutor executor;
    private final PingCircuitBreaker circuitBreaker;
    
    public PingMetricsController(StructuredLoggerFactory structuredLoggerFactory,
                               VirtualThreadPingService pingService,
                               VirtualThreadPingExecutor executor,
                               PingCircuitBreaker circuitBreaker) {
        this.logger = structuredLoggerFactory.getLogger(PingMetricsController.class);
        this.pingService = pingService;
        this.executor = executor;
        this.circuitBreaker = circuitBreaker;
    }
    
    /**
     * Get overall ping service metrics
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMetrics() {
        try {
            return ResponseEntity.ok(pingService.getMetrics());
        } catch (Exception e) {
            logger.with("endpoint", "getMetrics").with("error", e.getMessage()).error("Error retrieving metrics", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to retrieve metrics"));
        }
    }
    
    /**
     * Get executor-specific metrics
     */
    @GetMapping("/executor")
    public ResponseEntity<Map<String, Object>> getExecutorMetrics() {
        try {
            return ResponseEntity.ok(executor.getMetrics());
        } catch (Exception e) {
            logger.with("endpoint", "getExecutorMetrics").with("error", e.getMessage()).error("Error retrieving executor metrics", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to retrieve executor metrics"));
        }
    }
    
    /**
     * Get circuit breaker metrics
     */
    @GetMapping("/circuit-breaker")
    public ResponseEntity<Map<String, Object>> getCircuitBreakerMetrics() {
        try {
            return ResponseEntity.ok(circuitBreaker.getMetrics());
        } catch (Exception e) {
            logger.with("endpoint", "getCircuitBreakerMetrics").with("error", e.getMessage()).error("Error retrieving circuit breaker metrics", e);
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Failed to retrieve circuit breaker metrics"));
        }
    }
    
    /**
     * Reset circuit breaker for a specific device
     */
    @PostMapping("/circuit-breaker/reset/{deviceId}")
    public ResponseEntity<Map<String, String>> resetCircuitBreaker(@PathVariable UUID deviceId) {
        try {
            circuitBreaker.reset(deviceId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Circuit breaker reset for device " + deviceId
            ));
        } catch (Exception e) {
            logger.with("deviceId", deviceId).error("Error resetting circuit breaker for device", e);
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to reset circuit breaker"
                ));
        }
    }
    
    /**
     * Clear all circuit breakers
     */
    @PostMapping("/circuit-breaker/clear-all")
    public ResponseEntity<Map<String, String>> clearAllCircuitBreakers() {
        try {
            circuitBreaker.clearAll();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "All circuit breakers cleared"
            ));
        } catch (Exception e) {
            logger.with("endpoint", "clearAllCircuitBreakers").with("error", e.getMessage()).error("Error clearing all circuit breakers", e);
            return ResponseEntity.internalServerError()
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to clear circuit breakers"
                ));
        }
    }
}