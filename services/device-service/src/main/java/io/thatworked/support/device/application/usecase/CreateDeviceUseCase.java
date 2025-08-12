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
public class CreateDeviceUseCase {
    
    private final StructuredLogger logger;
    private final DeviceDomainService deviceDomainService;
    
    public CreateDeviceUseCase(StructuredLoggerFactory loggerFactory, DeviceDomainService deviceDomainService) {
        this.logger = loggerFactory.getLogger(CreateDeviceUseCase.class);
        this.deviceDomainService = deviceDomainService;
    }
    
    public DeviceDomain execute(String name, String ipAddress, String hostname, String macAddress,
                               String type, String description, String location,
                               String os, String osType, String make, String model,
                               String endpointId, String assetTag,
                               Map<String, String> metadata, Set<Long> roleIds) {
        logger.with("operation", "createDevice")
                .with("name", name)
                .with("ipAddress", ipAddress)
                .with("deviceType", type)
                .debug("Creating device");
        
        try {
            // Normalize MAC address to clean lowercase format for storage
            String normalizedMac = null;
            if (macAddress != null) {
                normalizedMac = FormatValidator.normalizeMacAddressForStorage(macAddress);
            }
            
            // Create device using domain service with all initial data
            DeviceDomain deviceDomain = deviceDomainService.createDevice(
                name,
                ipAddress,
                hostname,
                normalizedMac
            );
            
            // Update with additional fields using domain service
            DeviceDomain updatedDomain = deviceDomainService.updateDevice(
                deviceDomain.getId(),
                null, // name already set
                null, // ipAddress already set
                null, // hostname already set
                null, // macAddress already set
                type,
                description,
                location,
                os,
                osType,
                make,
                model,
                endpointId,
                assetTag,
                metadata
            );
            
            // Handle roles if provided (convert roleIds to roleNames)
            if (roleIds != null && !roleIds.isEmpty()) {
                // Convert roleIds to roleNames - assume roleIds are actually role names for now
                // This will need proper mapping when role management is fully implemented
                List<String> roleNames = roleIds.stream()
                    .map(Object::toString)
                    .toList();
                deviceDomainService.assignRoles(updatedDomain.getId(), roleNames);
            }
            
            // Return domain object - controller will handle DTO conversion
            logger.with("operation", "createDevice")
                    .with("deviceId", updatedDomain.getId())
                    .with("name", updatedDomain.getName())
                    .info("Device created successfully");
            return updatedDomain;
            
        } catch (Exception e) {
            logger.with("name", name)
                .with("ipAddress", ipAddress)
                .error("Failed to create device", e);
            throw e;
        }
    }
}