package io.thatworked.support.report;

import io.thatworked.support.report.config.ReportServiceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableKafka
@EnableScheduling
@EnableConfigurationProperties({
    ReportServiceConfig.class
})
public class ReportServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }
}