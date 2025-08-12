package io.thatworked.support.device.application.usecase;

import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.service.DeviceDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AssignDeviceToSiteUseCase {
    
    private final StructuredLogger logger;
    private final DeviceDomainService deviceDomainService;
    
    public AssignDeviceToSiteUseCase(StructuredLoggerFactory loggerFactory, DeviceDomainService deviceDomainService) {
        this.logger = loggerFactory.getLogger(AssignDeviceToSiteUseCase.class);
        this.deviceDomainService = deviceDomainService;
    }
    
    public DeviceDomain assignToSite(UUID deviceUuid, UUID siteId) {
        logger.with("operation", "assignDeviceToSite")
                .with("deviceUuid", deviceUuid)
                .with("siteId", siteId)
                .debug("Assigning device to site");
        
        // Use domain service for site assignment with business logic
        DeviceDomain updatedDomain = deviceDomainService.assignToSite(deviceUuid, siteId);
        
        logger.with("operation", "assignDeviceToSite")
                .with("deviceUuid", deviceUuid)
                .with("siteId", siteId)
                .with("deviceName", updatedDomain.getName())
                .info("Device assigned to site successfully");
        
        return updatedDomain;
    }
    
    public DeviceDomain removeFromSite(UUID deviceUuid) {
        logger.with("operation", "removeDeviceFromSite")
                .with("deviceUuid", deviceUuid)
                .debug("Removing device from site");
        
        // Use domain service for site removal with business logic
        DeviceDomain updatedDomain = deviceDomainService.removeFromSite(deviceUuid);
        
        logger.with("operation", "removeDeviceFromSite")
                .with("deviceUuid", deviceUuid)
                .with("deviceName", updatedDomain.getName())
                .info("Device removed from site successfully");
        
        return updatedDomain;
    }
    
    // Legacy method for backward compatibility
    public DeviceDomain execute(UUID deviceUuid, UUID siteId) {
        return assignToSite(deviceUuid, siteId);
    }
    
    // Business logic moved to domain service
    // Event publishing moved to domain service
    // Site assignment validation moved to domain service
}