package io.thatworked.support.device.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Service metadata and operational configuration.
 */
@Data
public class ServiceProperties {
    
    /**
     * Service name
     */
    @NotBlank(message = "Service name is required")
    private String name = "device-service";
    
    /**
     * Service version
     */
    @NotBlank(message = "Service version is required")
    private String version = "${project.version:0.0.1-SNAPSHOT}";
    
    /**
     * Service description
     */
    private String description = "Microservice for managing device inventory";
    
    /**
     * Health check configuration
     */
    @NotNull(message = "Health configuration is required")
    private HealthProperties health = new HealthProperties();
    
    /**
     * API configuration
     */
    @NotNull(message = "API configuration is required")
    private ApiProperties api = new ApiProperties();
    
    /**
     * Monitoring configuration
     */
    @NotNull(message = "Monitoring configuration is required")
    private MonitoringProperties monitoring = new MonitoringProperties();
    
    @Data
    public static class HealthProperties {
        /**
         * Enable health endpoint
         */
        private boolean enabled = true;
        
        /**
         * Health check interval
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration checkInterval = Duration.ofSeconds(30);
        
        /**
         * Include detailed information
         */
        private boolean showDetails = true;
        
        /**
         * Database health check timeout
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration dbTimeout = Duration.ofSeconds(5);
    }
    
    @Data
    public static class ApiProperties {
        /**
         * Enable API documentation
         */
        private boolean docsEnabled = true;
        
        /**
         * Max page size for pagination
         * TECH DEBT: Increased to 1000 to support loading all devices for client-side pagination
         * This is not scalable for production - see API Gateway QueryResolver for detailed notes
         */
        @Min(value = 1, message = "Max page size must be at least 1")
        private int maxPageSize = 1000;
        
        /**
         * Default page size
         */
        @Min(value = 1, message = "Default page size must be at least 1")
        private int defaultPageSize = 20;
        
        /**
         * Enable request/response logging
         */
        private boolean logRequests = false;
    }
    
    @Data
    public static class MonitoringProperties {
        /**
         * Enable metrics collection
         */
        private boolean enabled = true;
        
        /**
         * Enable distributed tracing
         */
        private boolean tracingEnabled = true;
        
        /**
         * Metrics prefix
         */
        @NotBlank(message = "Metrics prefix is required")
        private String metricsPrefix = "device_service";
        
        /**
         * Include host tag in metrics
         */
        private boolean includeHostTag = true;
        
        /**
         * Enable method-level metrics
         */
        private boolean methodMetrics = true;
    }
}