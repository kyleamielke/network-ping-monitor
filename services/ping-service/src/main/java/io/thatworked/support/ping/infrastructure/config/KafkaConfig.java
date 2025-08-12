package io.thatworked.support.ping.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {

    public static final String DEVICE_ALERTS_TOPIC = "device-alerts";
    public static final String DEVICE_DOWN_TOPIC = "device-down-alerts";
    public static final String DEVICE_RECOVERED_TOPIC = "device-recovered-alerts";
    public static final String DEVICE_EVENTS_TOPIC = "device-events";
    public static final String PING_MONITORING_EVENTS_TOPIC = "ping-monitoring-events";
    public static final String PING_RESULTS_TOPIC = "ping-results";

    @Bean
    public NewTopic deviceAlertsTopic() {
        return TopicBuilder.name(DEVICE_ALERTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deviceDownTopic() {
        return TopicBuilder.name(DEVICE_DOWN_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deviceRecoveredTopic() {
        return TopicBuilder.name(DEVICE_RECOVERED_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deviceEventsTopic() {
        return TopicBuilder.name(DEVICE_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic pingMonitoringEventsTopic() {
        return TopicBuilder.name(PING_MONITORING_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic pingResultsTopic() {
        return TopicBuilder.name(PING_RESULTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}