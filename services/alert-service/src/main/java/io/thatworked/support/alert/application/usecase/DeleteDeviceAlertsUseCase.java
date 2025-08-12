package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.port.AlertRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for deleting all alerts for a specific device.
 * This is typically used when a device is deleted from the system.
 */
@Service
public class DeleteDeviceAlertsUseCase {
    
    private final StructuredLogger logger;
    private final AlertRepository alertRepository;
    
    public DeleteDeviceAlertsUseCase(StructuredLoggerFactory loggerFactory, AlertRepository alertRepository) {
        this.logger = loggerFactory.getLogger(DeleteDeviceAlertsUseCase.class);
        this.alertRepository = alertRepository;
    }
    
    public void execute(UUID deviceId) {
        logger.with("operation", "deleteDeviceAlerts")
              .with("deviceId", deviceId)
              .debug("Deleting all alerts for device");
        
        alertRepository.deleteByDeviceId(deviceId);
        
        logger.with("operation", "deleteDeviceAlerts")
              .with("deviceId", deviceId)
              .info("All alerts deleted for device");
    }
}