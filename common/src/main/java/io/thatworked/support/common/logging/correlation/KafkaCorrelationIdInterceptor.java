package io.thatworked.support.common.logging.correlation;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Kafka interceptor to propagate correlation ID through events
 */
@Component
public class KafkaCorrelationIdInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaCorrelationIdInterceptor.class);
    
    /**
     * Add correlation ID to outgoing Kafka messages
     */
    public static <K, V> ProducerRecord<K, V> addCorrelationId(ProducerRecord<K, V> record) {
        String correlationId = MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY);
        
        if (correlationId == null) {
            correlationId = generateCorrelationId();
        }
        
        record.headers().add(
            CorrelationIdConstants.CORRELATION_ID_HEADER,
            correlationId.getBytes(StandardCharsets.UTF_8)
        );
        
        return record;
    }
    
    /**
     * Extract correlation ID from incoming Kafka messages
     */
    public static void extractCorrelationId(ConsumerRecord<?, ?> record) {
        try {
            Header correlationIdHeader = record.headers().lastHeader(CorrelationIdConstants.CORRELATION_ID_HEADER);
            
            if (correlationIdHeader != null && correlationIdHeader.value() != null) {
                String correlationId = new String(correlationIdHeader.value(), StandardCharsets.UTF_8);
                MDC.put(CorrelationIdConstants.CORRELATION_ID_MDC_KEY, correlationId);
            } else {
                // Generate new one if not present
                String correlationId = generateCorrelationId();
                MDC.put(CorrelationIdConstants.CORRELATION_ID_MDC_KEY, correlationId);
            }
        } catch (Exception e) {
            logger.warn("Failed to extract correlation ID from Kafka message, generating new one", e);
            String correlationId = generateCorrelationId();
            MDC.put(CorrelationIdConstants.CORRELATION_ID_MDC_KEY, correlationId);
        }
    }
    
    /**
     * Clean up MDC after processing
     */
    public static void clearCorrelationId() {
        MDC.remove(CorrelationIdConstants.CORRELATION_ID_MDC_KEY);
    }
    
    private static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}