package io.thatworked.support.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Health check configuration for API Gateway.
 * Provides basic health check infrastructure.
 */
@Configuration
public class HealthConfig {

    @Bean
    public RestTemplate healthCheckRestTemplate() {
        return new RestTemplate();
    }
}