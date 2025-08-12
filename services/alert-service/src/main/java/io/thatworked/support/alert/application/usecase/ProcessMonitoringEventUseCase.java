package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.service.AlertDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Use case for processing monitoring events and creating alerts based on rules.
 */
@Service
public class ProcessMonitoringEventUseCase {
    
    private final StructuredLogger logger;
    private final AlertDomainService alertDomainService;
    private final CreateAlertUseCase createAlertUseCase;
    
    public ProcessMonitoringEventUseCase(StructuredLoggerFactory loggerFactory, AlertDomainService alertDomainService, CreateAlertUseCase createAlertUseCase) {
        this.logger = loggerFactory.getLogger(ProcessMonitoringEventUseCase.class);
        this.alertDomainService = alertDomainService;
        this.createAlertUseCase = createAlertUseCase;
    }
    
    @Transactional
    public void execute(UUID deviceId, String deviceName, String eventType, String message, Instant timestamp,
                       String ipAddress, Integer consecutiveFailures, String failureReason) {
        logger.with("deviceId", deviceId)
              .with("eventType", eventType)
              .info("Processing monitoring event");
        
        try {
            switch (eventType) {
                case "DEVICE_DOWN":
                    createAlertUseCase.execute(deviceId, deviceName, AlertType.DEVICE_DOWN.name(), message,
                                             ipAddress, consecutiveFailures, failureReason);
                    break;
                    
                case "DEVICE_RECOVERED":
                    // Auto-resolve existing alerts for this device
                    alertDomainService.autoResolveDeviceAlerts(deviceId);
                    break;
                    
                case "HIGH_LATENCY":
                    createAlertUseCase.execute(deviceId, deviceName, AlertType.HIGH_RESPONSE_TIME.name(), message,
                                             ipAddress, null, null);
                    break;
                    
                case "PACKET_LOSS":
                    createAlertUseCase.execute(deviceId, deviceName, AlertType.PACKET_LOSS.name(), message,
                                             ipAddress, null, null);
                    break;
                    
                default:
                    logger.with("eventType", eventType)
                          .warn("Unknown monitoring event type");
            }
        } catch (Exception e) {
            logger.with("deviceId", deviceId)
                  .with("eventType", eventType)
                  .error("Failed to process monitoring event", e);
        }
    }
}