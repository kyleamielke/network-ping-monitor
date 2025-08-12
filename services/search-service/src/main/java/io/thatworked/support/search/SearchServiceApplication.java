package io.thatworked.support.search;

import io.thatworked.support.search.config.ServiceConfig;
import io.thatworked.support.search.infrastructure.config.SearchConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableKafka
@EnableConfigurationProperties({
    ServiceConfig.class,
    SearchConfig.class
})
public class SearchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
}