package io.thatworked.support.device.domain.service;

import io.thatworked.support.device.domain.exception.DeviceNotFoundDomainException;
import io.thatworked.support.device.domain.exception.DuplicateDeviceDomainException;
import io.thatworked.support.device.domain.exception.InvalidDeviceStateDomainException;
import io.thatworked.support.device.domain.exception.OptimisticLockingDomainException;
import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.model.DeviceRoleDomain;
import io.thatworked.support.device.domain.port.DeviceRepository;
import io.thatworked.support.device.domain.port.DomainLogger;
import io.thatworked.support.device.domain.port.EventPublisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain service containing core business logic for devices.
 * Pure domain service with no framework dependencies.
 * Depends only on domain ports which will be implemented by infrastructure.
 */
public class DeviceDomainService {
    
    private final DeviceRepository repository;
    private final EventPublisher eventPublisher;
    private final DomainLogger domainLogger;
    
    public DeviceDomainService(DeviceRepository repository, 
                              EventPublisher eventPublisher,
                              DomainLogger domainLogger) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.domainLogger = domainLogger;
    }
    
    /**
     * Create a new device.
     */
    public DeviceDomain createDevice(String name, String ipAddress, String hostname, String macAddress) {
        Map<String, Object> context = new HashMap<>();
        context.put("name", name);
        if (ipAddress != null) {
            context.put("ipAddress", ipAddress);
        }
        if (hostname != null) {
            context.put("hostname", hostname);
        }
        domainLogger.logBusinessEvent("Creating device", context);
        
        // Check for duplicates
        if (ipAddress != null && repository.existsByIpAddress(ipAddress)) {
            throw new DuplicateDeviceDomainException("ipAddress", ipAddress);
        }
        
        // MAC address should already be normalized by the use case layer
        if (macAddress != null && repository.existsByMacAddress(macAddress)) {
            throw new DuplicateDeviceDomainException("macAddress", macAddress);
        }
        
        // Create device
        DeviceDomain device = new DeviceDomain(name, ipAddress);
        if (macAddress != null || hostname != null) {
            device.updateDetails(name, ipAddress, hostname, macAddress);
        }
        
        // Save and publish event
        DeviceDomain saved = repository.save(device);
        eventPublisher.publishDeviceCreated(saved);
        
        domainLogger.logBusinessEvent("Device created successfully", Map.of(
            "deviceId", saved.getId(),
            "name", saved.getName()
        ));
        
        return saved;
    }
    
    /**
     * Update device with all supported fields.
     * Handles both basic and comprehensive updates by accepting null values for unchanged fields.
     */
    public DeviceDomain updateDevice(UUID deviceId, 
                                   String name, String ipAddress, String hostname,
                                   String macAddress, String deviceType, String description,
                                   String location, String os, String osType, String make,
                                   String model, String endpointId, String assetTag,
                                   Map<String, String> metadata) {
        return updateDevice(deviceId, name, ipAddress, hostname, macAddress, deviceType,
                          description, location, os, osType, make, model, endpointId,
                          assetTag, metadata, null);
    }
    
    /**
     * Updates an existing device with comprehensive details including optimistic locking.
     * Handles both basic and comprehensive updates by accepting null values for unchanged fields.
     */
    public DeviceDomain updateDevice(UUID deviceId, 
                                   String name, String ipAddress, String hostname,
                                   String macAddress, String deviceType, String description,
                                   String location, String os, String osType, String make,
                                   String model, String endpointId, String assetTag,
                                   Map<String, String> metadata, Long expectedVersion) {
        domainLogger.logBusinessEvent("Updating device", Map.of("deviceId", deviceId));
        
        DeviceDomain device = repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
        
        // Check optimistic locking version if provided
        if (expectedVersion != null && !expectedVersion.equals(device.getVersion())) {
            throw new OptimisticLockingDomainException("Device", deviceId.toString(), 
                    expectedVersion, device.getVersion());
        }
        
        // Validate unique constraints if changing
        if (ipAddress != null && !ipAddress.equals(device.getIpAddress())) {
            repository.findByIpAddress(ipAddress).ifPresent(existing -> {
                if (!existing.getId().equals(deviceId)) {
                    throw new DuplicateDeviceDomainException("ipAddress", ipAddress);
                }
            });
        }
        
        // MAC address should already be normalized by the use case layer
        if (macAddress != null && !macAddress.equals(device.getMacAddress())) {
            repository.findByMacAddress(macAddress).ifPresent(existing -> {
                if (!existing.getId().equals(deviceId)) {
                    throw new DuplicateDeviceDomainException("macAddress", macAddress);
                }
            });
        }
        
        // Track changes for auditing
        Map<String, Object> changes = new HashMap<>();
        
        // Update basic details if provided
        if (name != null || ipAddress != null || hostname != null || macAddress != null) {
            String currentName = device.getName();
            String currentIp = device.getIpAddress();
            String currentHostname = device.getHostname();
            String currentMac = device.getMacAddress();
            
            // Convert empty strings to null for clearing fields
            String nameToSet = (name != null && name.trim().isEmpty()) ? null : name;
            String ipToSet = (ipAddress != null && ipAddress.trim().isEmpty()) ? null : ipAddress;
            String hostnameToSet = (hostname != null && hostname.trim().isEmpty()) ? null : hostname;
            String macToSet = (macAddress != null && macAddress.trim().isEmpty()) ? null : macAddress;
            
            device.updateDetails(
                nameToSet != null ? nameToSet : currentName,
                ipToSet != null ? ipToSet : currentIp,
                hostnameToSet != null ? hostnameToSet : currentHostname,
                macToSet != null ? macToSet : currentMac
            );
            
            if (name != null && !Objects.equals(nameToSet, currentName)) {
                Map<String, String> nameChange = new HashMap<>();
                nameChange.put("old", currentName != null ? currentName : "");
                nameChange.put("new", nameToSet != null ? nameToSet : "");
                changes.put("name", nameChange);
            }
            if (ipAddress != null && !Objects.equals(ipToSet, currentIp)) {
                Map<String, String> ipChange = new HashMap<>();
                ipChange.put("old", currentIp != null ? currentIp : "");
                ipChange.put("new", ipToSet != null ? ipToSet : "");
                changes.put("ipAddress", ipChange);
            }
            if (hostname != null && !Objects.equals(hostnameToSet, currentHostname)) {
                Map<String, String> hostnameChange = new HashMap<>();
                hostnameChange.put("old", currentHostname != null ? currentHostname : "");
                hostnameChange.put("new", hostnameToSet != null ? hostnameToSet : "");
                changes.put("hostname", hostnameChange);
            }
            if (macAddress != null && !Objects.equals(macToSet, currentMac)) {
                Map<String, String> macChange = new HashMap<>();
                macChange.put("old", currentMac != null ? currentMac : "");
                macChange.put("new", macToSet != null ? macToSet : "");
                changes.put("macAddress", macChange);
            }
        }
        
        // Update system info if provided
        // Treat empty strings as null to allow clearing fields
        if (os != null || osType != null || make != null || model != null) {
            String oldOs = device.getOs();
            String oldOsType = device.getOsType();
            String oldMake = device.getMake();
            String oldModel = device.getModel();
            
            // Convert empty strings to null for clearing fields
            String osToSet = (os != null && os.trim().isEmpty()) ? null : os;
            String osTypeToSet = (osType != null && osType.trim().isEmpty()) ? null : osType;
            String makeToSet = (make != null && make.trim().isEmpty()) ? null : make;
            String modelToSet = (model != null && model.trim().isEmpty()) ? null : model;
            
            device.updateSystemInfo(osToSet, osTypeToSet, makeToSet, modelToSet);
            
            if (os != null && !Objects.equals(osToSet, oldOs)) {
                Map<String, String> osChange = new HashMap<>();
                osChange.put("old", oldOs != null ? oldOs : "");
                osChange.put("new", osToSet != null ? osToSet : "");
                changes.put("os", osChange);
            }
            if (osType != null && !Objects.equals(osTypeToSet, oldOsType)) {
                Map<String, String> osTypeChange = new HashMap<>();
                osTypeChange.put("old", oldOsType != null ? oldOsType : "");
                osTypeChange.put("new", osTypeToSet != null ? osTypeToSet : "");
                changes.put("osType", osTypeChange);
            }
            if (make != null && !Objects.equals(makeToSet, oldMake)) {
                Map<String, String> makeChange = new HashMap<>();
                makeChange.put("old", oldMake != null ? oldMake : "");
                makeChange.put("new", makeToSet != null ? makeToSet : "");
                changes.put("make", makeChange);
            }
            if (model != null && !Objects.equals(modelToSet, oldModel)) {
                Map<String, String> modelChange = new HashMap<>();
                modelChange.put("old", oldModel != null ? oldModel : "");
                modelChange.put("new", modelToSet != null ? modelToSet : "");
                changes.put("model", modelChange);
            }
        }
        
        // Update additional info if provided  
        if (deviceType != null) {
            String oldType = device.getType();
            String typeToSet = (deviceType.trim().isEmpty()) ? null : deviceType;
            device.updateAdditionalInfo(typeToSet, null, null);
            if (!Objects.equals(typeToSet, oldType)) {
                Map<String, String> typeChange = new HashMap<>();
                typeChange.put("old", oldType != null ? oldType : "");
                typeChange.put("new", typeToSet != null ? typeToSet : "");
                changes.put("deviceType", typeChange);
            }
        }
        
        // Update description and location if provided
        if (description != null || location != null) {
            String oldDescription = device.getDescription();
            String oldLocation = device.getLocation();
            // Convert empty strings to null for clearing fields
            String descToSet = (description != null && description.trim().isEmpty()) ? null : description;
            String locToSet = (location != null && location.trim().isEmpty()) ? null : location;
            device.setDescriptionAndLocation(descToSet, locToSet);
            if (description != null && !Objects.equals(descToSet, oldDescription)) {
                Map<String, String> descChange = new HashMap<>();
                descChange.put("old", oldDescription != null ? oldDescription : "");
                descChange.put("new", descToSet != null ? descToSet : "");
                changes.put("description", descChange);
            }
            if (location != null && !Objects.equals(locToSet, oldLocation)) {
                Map<String, String> locChange = new HashMap<>();
                locChange.put("old", oldLocation != null ? oldLocation : "");
                locChange.put("new", locToSet != null ? locToSet : "");
                changes.put("location", locChange);
            }
        }
        
        // Update metadata if provided
        if (metadata != null) {
            Map<String, String> oldMetadata = device.getMetadata();
            device.updateMetadata(metadata);
            changes.put("metadata", Map.of("old", oldMetadata, "new", metadata));
        }
        
        // Update identifiers if provided
        if (endpointId != null || assetTag != null) {
            String oldEndpointId = device.getEndpointId();
            String oldAssetTag = device.getAssetTag();
            // Convert empty strings to null for clearing fields
            String endpointIdToSet = (endpointId != null && endpointId.trim().isEmpty()) ? null : endpointId;
            String assetTagToSet = (assetTag != null && assetTag.trim().isEmpty()) ? null : assetTag;
            device.updateIdentifiers(endpointIdToSet, assetTagToSet);
            if (endpointId != null && !Objects.equals(endpointIdToSet, oldEndpointId)) {
                Map<String, String> endpointChange = new HashMap<>();
                endpointChange.put("old", oldEndpointId != null ? oldEndpointId : "");
                endpointChange.put("new", endpointIdToSet != null ? endpointIdToSet : "");
                changes.put("endpointId", endpointChange);
            }
            if (assetTag != null && !Objects.equals(assetTagToSet, oldAssetTag)) {
                Map<String, String> assetTagChange = new HashMap<>();
                assetTagChange.put("old", oldAssetTag != null ? oldAssetTag : "");
                assetTagChange.put("new", assetTagToSet != null ? assetTagToSet : "");
                changes.put("assetTag", assetTagChange);
            }
        }
        
        DeviceDomain saved = repository.save(device);
        
        // Publish event if there were changes
        if (!changes.isEmpty()) {
            eventPublisher.publishDeviceUpdated(saved, changes);
        }
        
        domainLogger.logBusinessEvent("Device updated successfully", Map.of(
            "deviceId", saved.getId(),
            "changesCount", changes.size(),
            "changes", changes
        ));
        
        return saved;
    }
    
    /**
     * Assign device to site.
     */
    public DeviceDomain assignToSite(UUID deviceId, UUID siteId) {
        domainLogger.logBusinessEvent("Assigning device to site", Map.of(
            "deviceId", deviceId,
            "siteId", siteId
        ));
        
        DeviceDomain device = repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
        
        UUID previousSiteId = device.getSiteId();
        device.assignToSite(siteId);
        DeviceDomain saved = repository.save(device);
        
        eventPublisher.publishDeviceAssignedToSite(saved, siteId);
        
        domainLogger.logDomainStateChange(
            "Device",
            deviceId.toString(),
            previousSiteId != null ? previousSiteId.toString() : "unassigned",
            siteId.toString(),
            Map.of("deviceName", device.getName())
        );
        
        return saved;
    }
    
    /**
     * Remove device from site.
     */
    public DeviceDomain removeFromSite(UUID deviceId) {
        domainLogger.logBusinessEvent("Removing device from site", Map.of("deviceId", deviceId));
        
        DeviceDomain device = repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
        
        if (!device.isAssignedToSite()) {
            throw new InvalidDeviceStateDomainException("Device is not assigned to any site");
        }
        
        UUID previousSiteId = device.getSiteId();
        device.removeFromSite();
        DeviceDomain saved = repository.save(device);
        
        eventPublisher.publishDeviceRemovedFromSite(saved, previousSiteId);
        
        domainLogger.logDomainStateChange(
            "Device",
            deviceId.toString(),
            previousSiteId.toString(),
            "unassigned",
            Map.of("deviceName", device.getName())
        );
        
        return saved;
    }
    
    /**
     * Activate a device.
     */
    public DeviceDomain activateDevice(UUID deviceId) {
        DeviceDomain device = repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
        
        String previousStatus = device.getStatus().name();
        device.activate();
        
        if (!previousStatus.equals(device.getStatus().name())) {
            DeviceDomain saved = repository.save(device);
            eventPublisher.publishDeviceActivated(saved);
            
            domainLogger.logDomainStateChange(
                "Device",
                deviceId.toString(),
                previousStatus,
                device.getStatus().name(),
                Map.of("deviceName", device.getName())
            );
            
            return saved;
        }
        
        return device;
    }
    
    /**
     * Deactivate a device.
     */
    public DeviceDomain deactivateDevice(UUID deviceId) {
        DeviceDomain device = repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
        
        String previousStatus = device.getStatus().name();
        device.deactivate();
        
        if (!previousStatus.equals(device.getStatus().name())) {
            DeviceDomain saved = repository.save(device);
            eventPublisher.publishDeviceDeactivated(saved);
            
            domainLogger.logDomainStateChange(
                "Device",
                deviceId.toString(),
                previousStatus,
                device.getStatus().name(),
                Map.of("deviceName", device.getName())
            );
            
            return saved;
        }
        
        return device;
    }
    
    /**
     * Delete a device.
     */
    public void deleteDevice(UUID deviceId) {
        domainLogger.logBusinessEvent("Deleting device", Map.of("deviceId", deviceId));
        
        // Get device before deletion to have info for the event
        DeviceDomain device = repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
        
        repository.deleteById(deviceId);
        eventPublisher.publishDeviceDeleted(device);
        
        domainLogger.logBusinessEvent("Device deleted successfully", Map.of(
            "deviceId", deviceId,
            "deviceName", device.getName()
        ));
    }
    
    /**
     * Find device by ID.
     */
    public DeviceDomain findById(UUID deviceId) {
        return repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
    }
    
    /**
     * Find device by UUID (returns Optional).
     */
    public Optional<DeviceDomain> findByUuid(UUID deviceId) {
        return repository.findById(deviceId);
    }
    
    /**
     * Find all devices.
     */
    public List<DeviceDomain> findAll() {
        return repository.findAll();
    }
    
    /**
     * Find devices by site.
     */
    public List<DeviceDomain> findBySite(UUID siteId) {
        return repository.findBySiteId(siteId);
    }
    
    /**
     * Find devices by type.
     */
    public List<DeviceDomain> findByType(String deviceType) {
        return repository.findByDeviceType(deviceType);
    }
    
    /**
     * Check if device exists by ID.
     */
    public boolean existsById(UUID deviceId) {
        return repository.existsById(deviceId);
    }
    
    /**
     * Check if IP address is taken by another device.
     */
    public boolean isIpAddressTaken(String ipAddress, UUID excludeDeviceId) {
        return repository.findByIpAddress(ipAddress)
            .map(device -> !device.getId().equals(excludeDeviceId))
            .orElse(false);
    }
    
    /**
     * Check if MAC address is taken by another device.
     */
    public boolean isMacAddressTaken(String macAddress, UUID excludeDeviceId) {
        return repository.findByMacAddress(macAddress)
            .map(device -> !device.getId().equals(excludeDeviceId))
            .orElse(false);
    }
    
    /**
     * Assign roles to device with business validation.
     */
    public DeviceDomain assignRoles(UUID deviceId, List<String> roleNames) {
        domainLogger.logBusinessEvent("Assigning roles to device", Map.of(
            "deviceId", deviceId,
            "roleNames", roleNames
        ));
        
        DeviceDomain device = repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
        
        // Validate role assignments based on device type
        validateRoleAssignments(device, roleNames);
        
        // Clear existing roles and add new ones
        device.getRoles().clear();
        for (String roleName : roleNames) {
            DeviceRoleDomain role = new DeviceRoleDomain(roleName, "Role assigned to device");
            device.addRole(role);
        }
        
        DeviceDomain saved = repository.save(device);
        eventPublisher.publishDeviceRolesUpdated(saved, roleNames);
        
        domainLogger.logBusinessEvent("Device roles assigned successfully", Map.of(
            "deviceId", saved.getId(),
            "roleCount", roleNames.size()
        ));
        
        return saved;
    }
    
    /**
     * Remove all roles from device.
     */
    public DeviceDomain removeAllRoles(UUID deviceId) {
        domainLogger.logBusinessEvent("Removing all roles from device", Map.of("deviceId", deviceId));
        
        DeviceDomain device = repository.findById(deviceId)
            .orElseThrow(() -> new DeviceNotFoundDomainException(deviceId));
        
        List<String> previousRoles = device.getRoles().stream()
            .map(DeviceRoleDomain::getName)
            .toList();
        
        device.getRoles().clear();
        DeviceDomain saved = repository.save(device);
        
        if (!previousRoles.isEmpty()) {
            eventPublisher.publishDeviceRolesCleared(saved, previousRoles);
        }
        
        domainLogger.logBusinessEvent("All roles removed from device", Map.of(
            "deviceId", saved.getId(),
            "removedRoleCount", previousRoles.size()
        ));
        
        return saved;
    }
    
    /**
     * Check if device has specific role.
     */
    public boolean hasRole(UUID deviceId, String roleName) {
        return repository.findById(deviceId)
            .map(device -> device.hasRole(roleName))
            .orElse(false);
    }
    
    /**
     * Get all roles for a device.
     */
    public List<String> getDeviceRoles(UUID deviceId) {
        return repository.findById(deviceId)
            .map(device -> device.getRoles().stream()
                .map(DeviceRoleDomain::getName)
                .toList())
            .orElse(List.of());
    }
    
    /**
     * Validate role assignments based on business rules.
     */
    private void validateRoleAssignments(DeviceDomain device, List<String> roleNames) {
        // Business rule: Validate role compatibility with device type
        String deviceType = device.getType();
        
        for (String roleName : roleNames) {
            // Example business rules - customize based on requirements
            if ("ROUTER".equals(deviceType) && "Server".equals(roleName)) {
                throw new InvalidDeviceStateDomainException(
                    "Cannot assign Server role to Router device type");
            }
            
            if ("SWITCH".equals(deviceType) && "Workstation".equals(roleName)) {
                throw new InvalidDeviceStateDomainException(
                    "Cannot assign Workstation role to Switch device type");
            }
        }
        
        // Business rule: Check for conflicting roles
        if (roleNames.contains("Router") && roleNames.contains("Switch")) {
            throw new InvalidDeviceStateDomainException(
                "Device cannot have both Router and Switch roles simultaneously");
        }
        
        if (roleNames.contains("Server") && roleNames.contains("Workstation")) {
            throw new InvalidDeviceStateDomainException(
                "Device cannot have both Server and Workstation roles simultaneously");
        }
        
        domainLogger.logBusinessEvent("Role assignment validation passed", Map.of(
            "deviceId", device.getId(),
            "deviceType", deviceType,
            "roleNames", roleNames
        ));
    }
}