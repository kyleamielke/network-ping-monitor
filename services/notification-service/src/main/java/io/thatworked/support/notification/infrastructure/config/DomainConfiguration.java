package io.thatworked.support.notification.infrastructure.config;

import io.thatworked.support.notification.domain.port.DomainLogger;
import io.thatworked.support.notification.domain.port.EventPublisher;
import io.thatworked.support.notification.domain.port.NotificationRepository;
import io.thatworked.support.notification.domain.port.NotificationSender;
import io.thatworked.support.notification.domain.service.NotificationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for domain layer components.
 */
@Configuration
public class DomainConfiguration {
    
    @Bean
    public NotificationDomainService notificationDomainService(
            NotificationRepository repository,
            NotificationSender sender,
            EventPublisher eventPublisher,
            DomainLogger logger) {
        return new NotificationDomainService(repository, sender, eventPublisher, logger);
    }
}