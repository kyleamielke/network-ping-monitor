package io.thatworked.support.search.infrastructure.config;

import io.thatworked.support.search.domain.port.CachePort;
import io.thatworked.support.search.domain.port.DomainLogger;
import io.thatworked.support.search.domain.service.SearchDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for domain layer components.
 * Creates domain services with their required dependencies.
 */
@Configuration
public class DomainConfiguration {
    
    @Bean
    public SearchDomainService searchDomainService(DomainLogger domainLogger, CachePort cachePort) {
        return new SearchDomainService(domainLogger, cachePort);
    }
}