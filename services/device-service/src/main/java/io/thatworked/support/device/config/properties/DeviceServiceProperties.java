package io.thatworked.support.device.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

/**
 * Main configuration properties for device service.
 * Provides type-safe access to all service configuration.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "device-service")
public class DeviceServiceProperties {
    
    /**
     * Database configuration
     */
    @Valid
    @NotNull(message = "Database configuration is required")
    @NestedConfigurationProperty
    private DatabaseProperties database = new DatabaseProperties();
    
    /**
     * Kafka messaging configuration
     */
    @Valid
    @NotNull(message = "Kafka configuration is required")
    @NestedConfigurationProperty
    private KafkaProperties kafka = new KafkaProperties();
    
    /**
     * Validation rules configuration
     */
    @Valid
    @NotNull(message = "Validation configuration is required")
    @NestedConfigurationProperty
    private ValidationProperties validation = new ValidationProperties();
    
    /**
     * Service metadata
     */
    @Valid
    @NotNull(message = "Service configuration is required")
    @NestedConfigurationProperty
    private ServiceProperties service = new ServiceProperties();
    
    /**
     * Feature flags
     */
    @Valid
    @NestedConfigurationProperty
    private FeatureProperties features = new FeatureProperties();
    
    /**
     * Resilience configuration
     */
    @Valid
    @NestedConfigurationProperty
    private ResilienceProperties resilience = new ResilienceProperties();
    
    /**
     * Observability configuration
     */
    @Valid
    @NestedConfigurationProperty
    private ObservabilityProperties observability = new ObservabilityProperties();
    
    /**
     * Thread pool configuration
     */
    @Valid
    @NestedConfigurationProperty
    private ThreadPoolProperties threadPool = new ThreadPoolProperties();
}