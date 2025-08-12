package io.thatworked.support.device.application.usecase;

import io.thatworked.support.device.domain.service.DeviceDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteDeviceUseCase {
    
    private final StructuredLogger logger;
    private final DeviceDomainService deviceDomainService;
    
    public DeleteDeviceUseCase(StructuredLoggerFactory loggerFactory, DeviceDomainService deviceDomainService) {
        this.logger = loggerFactory.getLogger(DeleteDeviceUseCase.class);
        this.deviceDomainService = deviceDomainService;
    }
    
    public void execute(UUID deviceUuid) {
        
        logger.with("operation", "deleteDevice")
                .with("deviceUuid", deviceUuid)
                .debug("Deleting device");
        
        // Remove roles first using domain service
        deviceDomainService.removeAllRoles(deviceUuid);
        
        // Delete device through domain service
        // This handles business logic validation, deletion, and event publishing
        deviceDomainService.deleteDevice(deviceUuid);
        
        logger.with("operation", "deleteDevice")
                .with("deviceUuid", deviceUuid)
                .info("Device deleted successfully");
    }
    
    // Business logic moved to domain service
    // Event publishing moved to domain service
    // Only infrastructure concerns (role cleanup) remain in use case
}