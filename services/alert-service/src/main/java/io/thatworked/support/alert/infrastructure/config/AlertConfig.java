package io.thatworked.support.alert.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "alert")
@Data
public class AlertConfig {

    private Rules rules = new Rules();
    private Cleanup cleanup = new Cleanup();

    @Bean
    public ObjectMapper alertObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Data
    public static class Rules {
        private DeviceDown deviceDown = new DeviceDown();
        private DeviceRecovery deviceRecovery = new DeviceRecovery();

        @Data
        public static class DeviceDown {
            private int failureThreshold = 3;
        }

        @Data
        public static class DeviceRecovery {
            private int successThreshold = 2;
        }
    }

    @Data
    public static class Cleanup {
        private int resolvedAlertsRetentionDays = 30;
        private int cleanupIntervalHours = 24;
    }
}