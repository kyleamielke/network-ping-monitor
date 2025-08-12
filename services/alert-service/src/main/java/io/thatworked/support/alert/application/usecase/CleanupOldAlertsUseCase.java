package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.service.AlertDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Use case for cleaning up old alerts based on retention policy.
 */
@Service
public class CleanupOldAlertsUseCase {
    
    private final StructuredLogger logger;
    private final AlertDomainService alertDomainService;
    
    public CleanupOldAlertsUseCase(StructuredLoggerFactory loggerFactory, AlertDomainService alertDomainService) {
        this.logger = loggerFactory.getLogger(CleanupOldAlertsUseCase.class);
        this.alertDomainService = alertDomainService;
    }
    
    public int execute() {
        logger.with("operation", "cleanupOldAlerts")
              .debug("Starting alert cleanup");
        
        int deletedCount = alertDomainService.cleanupOldAlerts();
        
        logger.with("operation", "cleanupOldAlerts")
              .with("deletedCount", deletedCount)
              .info("Completed alert cleanup");
        
        return deletedCount;
    }
}