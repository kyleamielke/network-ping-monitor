package io.thatworked.support.alert;

import io.thatworked.support.alert.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
@EnableScheduling
@EnableConfigurationProperties({
    KafkaTopicsConfig.class,
    EventConfig.class,
    MessagesConfig.class,
    ServiceConfig.class,
    BusinessRulesConfig.class
})
public class AlertServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertServiceApplication.class, args);
    }
}