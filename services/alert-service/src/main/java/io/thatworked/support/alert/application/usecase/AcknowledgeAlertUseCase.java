package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.port.EventPublisher;
import io.thatworked.support.alert.domain.service.AlertDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for acknowledging alerts.
 */
@Service
public class AcknowledgeAlertUseCase {
    
    private final StructuredLogger logger;
    private final AlertDomainService alertDomainService;
    private final EventPublisher eventPublisher;
    
    public AcknowledgeAlertUseCase(StructuredLoggerFactory loggerFactory, AlertDomainService alertDomainService, EventPublisher eventPublisher) {
        this.logger = loggerFactory.getLogger(AcknowledgeAlertUseCase.class);
        this.alertDomainService = alertDomainService;
        this.eventPublisher = eventPublisher;
    }
    
    public AlertDomain execute(UUID alertId, String acknowledgedBy) {
        logger.with("operation", "acknowledgeAlert")
              .with("alertId", alertId)
              .with("acknowledgedBy", acknowledgedBy)
              .debug("Acknowledging alert");
        
        // Acknowledge through domain service
        AlertDomain alert = alertDomainService.acknowledgeAlert(alertId, acknowledgedBy);
        
        // Publish alert acknowledged event
        eventPublisher.publishAlertAcknowledged(alert);
        
        logger.with("operation", "acknowledgeAlert")
              .with("alertId", alertId)
              .info("Alert acknowledged successfully");
        
        return alert;
    }
}