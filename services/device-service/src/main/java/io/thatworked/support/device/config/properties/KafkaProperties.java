package io.thatworked.support.device.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Kafka messaging configuration properties.
 */
@Data
public class KafkaProperties {
    
    /**
     * Topic configuration
     */
    @NotNull(message = "Topic configuration is required")
    private TopicProperties topic = new TopicProperties();
    
    /**
     * Consumer configuration
     */
    @NotNull(message = "Consumer configuration is required")
    private ConsumerProperties consumer = new ConsumerProperties();
    
    /**
     * Producer configuration
     */
    @NotNull(message = "Producer configuration is required")
    private ProducerProperties producer = new ProducerProperties();
    
    /**
     * Enable event publishing
     */
    private boolean enabled = true;
    
    @Data
    public static class TopicProperties {
        /**
         * Device events topic name
         */
        @NotBlank(message = "Device events topic is required")
        private String deviceEvents = "device-events";
        
        /**
         * Number of partitions
         */
        @Min(value = 1, message = "Partitions must be at least 1")
        private int partitions = 3;
        
        /**
         * Replication factor
         */
        @Min(value = 1, message = "Replication factor must be at least 1")
        private short replicationFactor = 1;
    }
    
    @Data
    public static class ConsumerProperties {
        /**
         * Consumer group ID
         */
        @NotBlank(message = "Consumer group is required")
        private String groupId = "device-service";
        
        /**
         * Enable auto commit
         */
        private boolean autoCommit = false;
        
        /**
         * Session timeout
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration sessionTimeout = Duration.ofSeconds(30);
        
        /**
         * Max poll records
         */
        @Min(value = 1, message = "Max poll records must be at least 1")
        private int maxPollRecords = 500;
    }
    
    @Data
    public static class ProducerProperties {
        /**
         * Request timeout
         */
        @DurationUnit(ChronoUnit.SECONDS)
        private Duration requestTimeout = Duration.ofSeconds(30);
        
        /**
         * Retry attempts
         */
        @Min(value = 0, message = "Retries cannot be negative")
        private int retries = 3;
        
        /**
         * Batch size
         */
        @Min(value = 0, message = "Batch size cannot be negative")
        private int batchSize = 16384;
        
        /**
         * Linger time for batching
         */
        @DurationUnit(ChronoUnit.MILLIS)
        private Duration lingerTime = Duration.ofMillis(5);
        
        /**
         * Compression type
         */
        private String compressionType = "snappy";
    }
}