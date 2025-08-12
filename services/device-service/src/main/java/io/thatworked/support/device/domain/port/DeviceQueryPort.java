package io.thatworked.support.device.domain.port;

import io.thatworked.support.device.domain.model.DeviceDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for querying devices.
 * Defines query operations available in the domain layer.
 * Implementation will be provided by infrastructure layer.
 */
public interface DeviceQueryPort {
    
    Optional<DeviceDomain> findByUuid(UUID uuid);
    
    List<DeviceDomain> findByUuids(List<UUID> uuids);
    
    List<DeviceDomain> findAll();
    
    List<DeviceDomain> findByType(String deviceType);
    
    List<DeviceDomain> findBySite(UUID siteId);
    
    List<DeviceDomain> findBySiteAndType(UUID siteId, String deviceType);
    
    List<DeviceDomain> findUnassigned();
    
    long count();
    
    long countByType(String deviceType);
    
    long countBySite(UUID siteId);
    
    boolean existsByUuid(UUID uuid);
    
    boolean existsByIpAddress(String ipAddress);
}