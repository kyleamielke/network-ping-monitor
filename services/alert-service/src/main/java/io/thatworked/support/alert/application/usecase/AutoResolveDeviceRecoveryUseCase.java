package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.port.EventPublisher;
import io.thatworked.support.alert.domain.service.AlertDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Use case for auto-resolving alerts when a device recovers.
 */
@Service
public class AutoResolveDeviceRecoveryUseCase {
    
    private final StructuredLogger logger;
    private final AlertDomainService alertDomainService;
    private final EventPublisher eventPublisher;
    
    public AutoResolveDeviceRecoveryUseCase(StructuredLoggerFactory loggerFactory, AlertDomainService alertDomainService, EventPublisher eventPublisher) {
        this.logger = loggerFactory.getLogger(AutoResolveDeviceRecoveryUseCase.class);
        this.alertDomainService = alertDomainService;
        this.eventPublisher = eventPublisher;
    }
    
    public List<AlertDomain> execute(UUID deviceId) {
        logger.with("operation", "autoResolveDeviceRecovery")
              .with("deviceId", deviceId)
              .debug("Auto-resolving alerts for recovered device");
        
        // Auto-resolve through domain service
        List<AlertDomain> resolvedAlerts = alertDomainService.autoResolveDeviceAlerts(deviceId);
        
        // Publish resolved events for each alert
        resolvedAlerts.stream()
            .filter(AlertDomain::isResolved)
            .forEach(eventPublisher::publishAlertResolved);
        
        logger.with("operation", "autoResolveDeviceRecovery")
              .with("deviceId", deviceId)
              .with("resolvedCount", resolvedAlerts.stream().filter(AlertDomain::isResolved).count())
              .info("Auto-resolved alerts for recovered device");
        
        return resolvedAlerts;
    }
}