package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.port.EventPublisher;
import io.thatworked.support.alert.domain.service.AlertDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Use case for creating new alerts.
 */
@Service
public class CreateAlertUseCase {
    
    private final StructuredLogger logger;
    private final AlertDomainService alertDomainService;
    private final EventPublisher eventPublisher;
    
    public CreateAlertUseCase(StructuredLoggerFactory loggerFactory, AlertDomainService alertDomainService, EventPublisher eventPublisher) {
        this.logger = loggerFactory.getLogger(CreateAlertUseCase.class);
        this.alertDomainService = alertDomainService;
        this.eventPublisher = eventPublisher;
    }
    
    public AlertDomain execute(UUID deviceId, String deviceName, String alertType, String message) {
        logger.with("operation", "createAlert")
              .with("deviceId", deviceId)
              .with("alertType", alertType)
              .debug("Creating new alert");
        
        // Convert string to AlertType enum
        AlertType type = AlertType.valueOf(alertType);
        
        // Create alert through domain service
        AlertDomain alert = alertDomainService.createAlert(deviceId, deviceName, type, message);
        
        // Publish alert created event
        eventPublisher.publishAlertCreated(alert);
        
        logger.with("operation", "createAlert")
              .with("alertId", alert.getId())
              .with("deviceId", deviceId)
              .info("Alert created successfully");
        
        return alert;
    }
    
    public AlertDomain execute(UUID deviceId, String deviceName, String alertType, String message,
                             String ipAddress, Integer consecutiveFailures, String failureReason) {
        logger.with("operation", "createAlert")
              .with("deviceId", deviceId)
              .with("alertType", alertType)
              .with("ipAddress", ipAddress)
              .debug("Creating new alert with metadata");
        
        // Convert string to AlertType enum
        AlertType type = AlertType.valueOf(alertType);
        
        // Create alert through domain service with metadata
        AlertDomain alert = alertDomainService.createAlert(deviceId, deviceName, type, message, 
                                                          ipAddress, consecutiveFailures, failureReason);
        
        // Publish domain event
        eventPublisher.publishAlertCreated(alert);
        
        logger.with("operation", "createAlert")
              .with("alertId", alert.getId())
              .info("Alert created successfully with metadata");
        
        return alert;
    }
}