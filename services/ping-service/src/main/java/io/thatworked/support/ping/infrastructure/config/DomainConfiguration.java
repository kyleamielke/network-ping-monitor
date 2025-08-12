package io.thatworked.support.ping.infrastructure.config;

import io.thatworked.support.ping.application.usecase.*;
import io.thatworked.support.ping.config.AlertingConfig;
import io.thatworked.support.ping.domain.port.*;
import io.thatworked.support.ping.domain.service.PingMonitoringDomainService;
import io.thatworked.support.ping.domain.service.PingTargetDomainService;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Clean Architecture dependency injection.
 * Wires together domain services, use cases, and ports.
 */
@Configuration
public class DomainConfiguration {
    
    private final AlertingConfig alertingConfig;
    
    public DomainConfiguration(AlertingConfig alertingConfig) {
        this.alertingConfig = alertingConfig;
    }
    
    // Domain Services
    @Bean
    public PingTargetDomainService pingTargetDomainService(
            PingTargetRepository pingTargetRepository,
            DeviceClient deviceClient,
            EventPublisher eventPublisher,
            DomainLogger domainLogger) {
        return new PingTargetDomainService(
            pingTargetRepository,
            deviceClient,
            eventPublisher,
            domainLogger
        );
    }
    
    @Bean
    public PingMonitoringDomainService pingMonitoringDomainService(
            PingResultRepository pingResultRepository,
            AlertStateRepository alertStateRepository,
            PingTargetRepository pingTargetRepository,
            DeviceClient deviceClient,
            EventPublisher eventPublisher,
            DomainLogger domainLogger) {
        return new PingMonitoringDomainService(
            pingResultRepository,
            alertStateRepository,
            pingTargetRepository,
            deviceClient,
            eventPublisher,
            domainLogger,
            alertingConfig.getFailureThreshold(),
            alertingConfig.getRecoveryThreshold()
        );
    }
    
    // Use Cases
    @Bean
    public CreatePingTargetUseCase createPingTargetUseCase(
            PingTargetDomainService pingTargetDomainService,
            StructuredLoggerFactory structuredLoggerFactory) {
        return new CreatePingTargetUseCase(pingTargetDomainService, structuredLoggerFactory);
    }
    
    @Bean
    public StartPingMonitoringUseCase startPingMonitoringUseCase(
            PingTargetDomainService pingTargetDomainService) {
        return new StartPingMonitoringUseCase(pingTargetDomainService);
    }
    
    @Bean
    public StopPingMonitoringUseCase stopPingMonitoringUseCase(
            PingTargetDomainService pingTargetDomainService) {
        return new StopPingMonitoringUseCase(pingTargetDomainService);
    }
    
    @Bean
    public GetPingTargetsUseCase getPingTargetsUseCase(
            PingTargetQueryPort pingTargetQueryPort) {
        return new GetPingTargetsUseCase(pingTargetQueryPort);
    }
    
    @Bean
    public ProcessPingResultUseCase processPingResultUseCase(
            PingMonitoringDomainService pingMonitoringDomainService) {
        return new ProcessPingResultUseCase(pingMonitoringDomainService);
    }
    
    @Bean
    public CleanupDeviceDataUseCase cleanupDeviceDataUseCase(
            PingTargetDomainService pingTargetDomainService,
            PingMonitoringDomainService pingMonitoringDomainService) {
        return new CleanupDeviceDataUseCase(pingTargetDomainService, pingMonitoringDomainService);
    }
}