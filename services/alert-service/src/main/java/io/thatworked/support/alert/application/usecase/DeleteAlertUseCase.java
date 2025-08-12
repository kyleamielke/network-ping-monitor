package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.port.AlertRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for deleting alerts.
 */
@Service
public class DeleteAlertUseCase {
    
    private final StructuredLogger logger;
    private final AlertRepository alertRepository;
    
    public DeleteAlertUseCase(StructuredLoggerFactory loggerFactory, AlertRepository alertRepository) {
        this.logger = loggerFactory.getLogger(DeleteAlertUseCase.class);
        this.alertRepository = alertRepository;
    }
    
    public void execute(UUID alertId) {
        logger.with("operation", "deleteAlert")
              .with("alertId", alertId)
              .debug("Deleting alert");
        
        alertRepository.deleteById(alertId);
        
        logger.with("operation", "deleteAlert")
              .with("alertId", alertId)
              .info("Alert deleted successfully");
    }
}