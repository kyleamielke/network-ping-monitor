package io.thatworked.support.alert.infrastructure.config;

import io.thatworked.support.alert.application.usecase.GetAlertsUseCase;
import io.thatworked.support.alert.config.BusinessRulesConfig;
import io.thatworked.support.alert.config.MessagesConfig;
import io.thatworked.support.alert.domain.port.AlertQueryPort;
import io.thatworked.support.alert.domain.port.AlertRepository;
import io.thatworked.support.alert.domain.port.DomainLogger;
import io.thatworked.support.alert.domain.service.AlertDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for domain services and use cases.
 * Wires up domain services with their required dependencies.
 */
@Configuration
public class DomainConfiguration {
    
    @Bean
    public AlertDomainService alertDomainService(AlertRepository alertRepository, DomainLogger domainLogger,
                                                BusinessRulesConfig businessRulesConfig, MessagesConfig messagesConfig) {
        return new AlertDomainService(alertRepository, domainLogger, businessRulesConfig, messagesConfig);
    }
    
    @Bean
    public GetAlertsUseCase getAlertsUseCase(AlertQueryPort alertQueryPort) {
        return new GetAlertsUseCase(alertQueryPort);
    }
}