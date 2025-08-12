package io.thatworked.support.gateway.config;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * GraphQL configuration for custom scalars and runtime wiring.
 */
@Configuration
public class GraphQLConfig {
    
    private final StructuredLogger logger;
    
    public GraphQLConfig(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(GraphQLConfig.class);
    }
    
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> {
            logger.with("operation", "configureRuntimeWiring")
                  .info("Configuring GraphQL runtime wiring with custom scalars");
            
            // Use our custom Instant scalar instead of ExtendedScalars.DateTime
            // This handles java.time.Instant which is what our services return
            wiringBuilder.scalar(InstantScalar.INSTANT);
        };
    }
}