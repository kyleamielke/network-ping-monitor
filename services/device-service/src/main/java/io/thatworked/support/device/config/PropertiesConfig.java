package io.thatworked.support.device.config;

import io.thatworked.support.device.config.properties.DeviceServiceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to enable type-safe configuration properties.
 */
@Configuration
@EnableConfigurationProperties(DeviceServiceProperties.class)
public class PropertiesConfig {
    // Configuration properties are automatically registered as beans
}