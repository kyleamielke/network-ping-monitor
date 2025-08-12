package io.thatworked.support.device.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Observability configuration for metrics, tracing, and logging.
 */
@Data
public class ObservabilityProperties {
    
    /**
     * Metrics configuration
     */
    @NotNull(message = "Metrics configuration is required")
    private MetricsProperties metrics = new MetricsProperties();
    
    /**
     * Tracing configuration
     */
    @NotNull(message = "Tracing configuration is required")
    private TracingProperties tracing = new TracingProperties();
    
    /**
     * Logging configuration
     */
    @NotNull(message = "Logging configuration is required")
    private LoggingProperties logging = new LoggingProperties();
    
    @Data
    public static class MetricsProperties {
        /**
         * Enable metrics collection
         */
        private boolean enabled = true;
        
        /**
         * Metrics export interval in seconds
         */
        @Min(value = 1, message = "Export interval must be at least 1 second")
        private int exportInterval = 60;
        
        /**
         * Include JVM metrics
         */
        private boolean includeJvmMetrics = true;
        
        /**
         * Include system metrics
         */
        private boolean includeSystemMetrics = true;
        
        /**
         * Custom tags to add to all metrics
         */
        private Map<String, String> tags = new HashMap<>();
        
        /**
         * Percentiles to calculate for distribution statistics
         */
        private List<Double> percentiles = List.of(0.5, 0.75, 0.95, 0.99);
    }
    
    @Data
    public static class TracingProperties {
        /**
         * Enable distributed tracing
         */
        private boolean enabled = true;
        
        /**
         * Sampling probability (0.0 to 1.0)
         */
        @Min(value = 0, message = "Sampling probability cannot be negative")
        @Max(value = 1, message = "Sampling probability cannot exceed 1")
        private double samplingProbability = 0.1;
        
        /**
         * Service name for traces
         */
        @NotBlank(message = "Service name is required")
        private String serviceName = "${spring.application.name:device-service}";
        
        /**
         * Include request headers in traces
         */
        private boolean includeRequestHeaders = false;
        
        /**
         * Include response headers in traces
         */
        private boolean includeResponseHeaders = false;
    }
    
    @Data
    public static class LoggingProperties {
        /**
         * Enable structured logging
         */
        private boolean structuredLogging = true;
        
        /**
         * Include correlation ID in logs
         */
        private boolean includeCorrelationId = true;
        
        /**
         * Include trace ID in logs
         */
        private boolean includeTraceId = true;
        
        /**
         * Log level for application packages
         */
        @NotBlank(message = "Application log level is required")
        private String applicationLevel = "INFO";
        
        /**
         * Log level for framework packages
         */
        @NotBlank(message = "Framework log level is required")
        private String frameworkLevel = "WARN";
        
        /**
         * Enable request/response logging
         */
        private boolean logRequests = false;
        
        /**
         * Sanitize sensitive data in logs
         */
        private boolean sanitizeSensitiveData = true;
    }
}