package io.thatworked.support.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration to allow frontend applications to access the GraphQL API.
 * Configurable via application properties for different environments.
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-methods}")
    private List<String> allowedMethods;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    // Commenting out CorsConfigurationSource to avoid conflicts
    // Using only WebMvcConfigurer approach

    /**
     * Alternative CORS configuration using WebMvcConfigurer.
     * This ensures CORS is applied at the Spring MVC level.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(allowedOrigins.toArray(new String[0]))
                        .allowedMethods(allowedMethods.toArray(new String[0]))
                        .allowedHeaders("*")
                        .allowCredentials(allowCredentials)
                        .maxAge(maxAge);
            }
        };
    }
}