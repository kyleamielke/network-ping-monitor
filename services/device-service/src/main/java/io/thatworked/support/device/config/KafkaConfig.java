package io.thatworked.support.device.config;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.device.config.properties.DeviceServiceProperties;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import jakarta.annotation.PostConstruct;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {
    
    private final StructuredLogger logger;
    private final DeviceServiceProperties properties;
    
    public KafkaConfig(StructuredLoggerFactory loggerFactory, DeviceServiceProperties properties) {
        this.logger = loggerFactory.getLogger(KafkaConfig.class);
        this.properties = properties;
    }

    @Value("${spring.kafka.bootstrap-servers:NOT_SET}")
    private String bootstrapServers;

    @PostConstruct
    public void logKafkaConfig() {
        logger.with("configuredBootstrapServers", bootstrapServers)
                .with("envBootstrapServers", System.getenv("KAFKA_BOOTSTRAP_SERVERS"))
                .info("Kafka configuration initialized");
    }


    @Bean
    public NewTopic deviceEventsTopic() {
        var kafkaProps = properties.getKafka().getTopic();
        
        return TopicBuilder.name(kafkaProps.getDeviceEvents())
                .partitions(kafkaProps.getPartitions())
                .replicas(kafkaProps.getReplicationFactor())
                .build();
    }
}