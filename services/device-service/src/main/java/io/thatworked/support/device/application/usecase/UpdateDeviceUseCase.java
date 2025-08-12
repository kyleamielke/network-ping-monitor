package io.thatworked.support.device.application.usecase;

import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.service.DeviceDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.common.validation.FormatValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class UpdateDeviceUseCase {
    
    private final StructuredLogger logger;
    private final DeviceDomainService deviceDomainService;
    
    public UpdateDeviceUseCase(StructuredLoggerFactory loggerFactory, DeviceDomainService deviceDomainService) {
        this.logger = loggerFactory.getLogger(UpdateDeviceUseCase.class);
        this.deviceDomainService = deviceDomainService;
    }
    
    public DeviceDomain execute(UUID deviceUuid, String name, String ipAddress, String hostname,
                               String macAddress, String type, String description, String location,
                               String os, String osType, String make, String model,
                               String endpointId, String assetTag,
                               Map<String, String> metadata, Set<Long> roleIds, Long version) {
        
        logger.with("operation", "updateDevice")
                .with("deviceUuid", deviceUuid)
                .with("name", name)
                .with("ipAddress", ipAddress)
                .with("hostname", hostname)
                .debug("Updating device");
        
        // Normalize MAC address to clean lowercase format for storage
        String normalizedMac = null;
        if (macAddress != null) {
            normalizedMac = FormatValidator.normalizeMacAddressForStorage(macAddress);
        }
        
        // Use domain service for update with business logic
        DeviceDomain updatedDomain = deviceDomainService.updateDevice(
            deviceUuid,
            name,
            ipAddress,
            hostname,
            normalizedMac,
            type,
            description,
            location,
            os,
            osType,
            make,
            model,
            endpointId,
            assetTag,
            metadata,
            version
        );
        
        // Update roles if provided using domain service
        DeviceDomain finalDomain = updatedDomain;
        if (roleIds != null) {
            // Convert roleIds to roleNames - assume roleIds are actually role names for now
            // This will need proper mapping when role management is fully implemented
            List<String> roleNames = roleIds.stream()
                .map(Object::toString)
                .toList();
            finalDomain = deviceDomainService.assignRoles(deviceUuid, roleNames);
        }
        
        logger.with("operation", "updateDevice")
                .with("deviceUuid", deviceUuid)
                .with("deviceName", finalDomain.getName())
                .info("Device updated successfully");
        
        return finalDomain;
    }
    
    // Business logic moved to domain service
    // Event publishing moved to domain service
    // Field updates moved to domain service
    // All infrastructure concerns kept here (JPA entity operations, role updates)
}