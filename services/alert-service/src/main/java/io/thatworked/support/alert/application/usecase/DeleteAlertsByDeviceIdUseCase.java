package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.port.AlertRepository;
import io.thatworked.support.alert.domain.port.EventPublisher;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Use case for deleting all alerts for a specific device.
 */
@Service
public class DeleteAlertsByDeviceIdUseCase {
    
    private final StructuredLogger logger;
    private final AlertRepository alertRepository;
    private final EventPublisher eventPublisher;
    
    public DeleteAlertsByDeviceIdUseCase(StructuredLoggerFactory loggerFactory, AlertRepository alertRepository, EventPublisher eventPublisher) {
        this.logger = loggerFactory.getLogger(DeleteAlertsByDeviceIdUseCase.class);
        this.alertRepository = alertRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Transactional
    public int execute(UUID deviceId) {
        logger.with("deviceId", deviceId)
              .info("Deleting all alerts for device");
        
        // Find all alerts for this device
        List<AlertDomain> alerts = alertRepository.findByDeviceId(deviceId);
        
        // Delete each alert
        alerts.forEach(alert -> {
            alertRepository.deleteById(alert.getId());
            // Note: We could publish delete events here if needed
        });
        
        logger.with("deviceId", deviceId)
              .with("deletedCount", alerts.size())
              .info("Deleted alerts for device");
        
        return alerts.size();
    }
}