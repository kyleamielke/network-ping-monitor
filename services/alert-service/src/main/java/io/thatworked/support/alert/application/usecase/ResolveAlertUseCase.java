package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.port.EventPublisher;
import io.thatworked.support.alert.domain.service.AlertDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for resolving alerts.
 */
@Service
public class ResolveAlertUseCase {
    
    private final StructuredLogger logger;
    private final AlertDomainService alertDomainService;
    private final EventPublisher eventPublisher;
    
    public ResolveAlertUseCase(StructuredLoggerFactory loggerFactory, AlertDomainService alertDomainService, EventPublisher eventPublisher) {
        this.logger = loggerFactory.getLogger(ResolveAlertUseCase.class);
        this.alertDomainService = alertDomainService;
        this.eventPublisher = eventPublisher;
    }
    
    public AlertDomain execute(UUID alertId) {
        logger.with("operation", "resolveAlert")
              .with("alertId", alertId)
              .debug("Resolving alert");
        
        // Resolve through domain service
        AlertDomain alert = alertDomainService.resolveAlert(alertId);
        
        // Publish alert resolved event
        eventPublisher.publishAlertResolved(alert);
        
        logger.with("operation", "resolveAlert")
              .with("alertId", alertId)
              .info("Alert resolved successfully");
        
        return alert;
    }
}