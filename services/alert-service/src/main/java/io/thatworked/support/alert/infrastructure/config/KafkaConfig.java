package io.thatworked.support.alert.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaConfig {

    // Topics consumed by Alert Service
    public static final String PING_MONITORING_EVENTS_TOPIC = "ping-monitoring-events";
    public static final String SNMP_MONITORING_EVENTS_TOPIC = "snmp-monitoring-events";
    public static final String WMI_MONITORING_EVENTS_TOPIC = "wmi-monitoring-events";
    
    // Topics produced by Alert Service
    public static final String ALERT_LIFECYCLE_EVENTS_TOPIC = "alert-lifecycle-events";
    public static final String ALERT_NOTIFICATIONS_TOPIC = "alert-notifications";

    @Bean
    public NewTopic pingMonitoringEventsTopic() {
        return TopicBuilder.name(PING_MONITORING_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic snmpMonitoringEventsTopic() {
        return TopicBuilder.name(SNMP_MONITORING_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic wmiMonitoringEventsTopic() {
        return TopicBuilder.name(WMI_MONITORING_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertLifecycleEventsTopic() {
        return TopicBuilder.name(ALERT_LIFECYCLE_EVENTS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic alertNotificationsTopic() {
        return TopicBuilder.name(ALERT_NOTIFICATIONS_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}