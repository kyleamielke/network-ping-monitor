package io.thatworked.support.device.infrastructure.config;

import io.thatworked.support.device.domain.port.DeviceRepository;
import io.thatworked.support.device.domain.port.DomainLogger;
import io.thatworked.support.device.domain.port.EventPublisher;
import io.thatworked.support.device.domain.service.DeviceDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for domain services and their dependencies.
 */
@Configuration
public class DomainConfiguration {
    
    @Bean
    public DeviceDomainService deviceDomainService(
            DeviceRepository deviceRepository,
            EventPublisher eventPublisher,
            DomainLogger domainLogger) {
        return new DeviceDomainService(deviceRepository, eventPublisher, domainLogger);
    }
}