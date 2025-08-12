package io.thatworked.support.common.logging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.thatworked.support.common.logging.correlation.CorrelationIdFilter;
import io.thatworked.support.common.logging.correlation.KafkaCorrelationIdInterceptor;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Auto-configuration for standardized logging
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.boot.autoconfigure.SpringBootApplication")
@Import({CorrelationIdFilter.class, KafkaCorrelationIdInterceptor.class})
public class LoggingConfiguration {
    
    @Getter
    private static final ObjectMapper objectMapper = createObjectMapper();
    
    @Bean
    @ConditionalOnMissingBean
    public StructuredLoggerFactory structuredLoggerFactory() {
        return new StructuredLoggerFactory();
    }
    
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return mapper;
    }
}