package io.thatworked.support.device.domain.port;

import io.thatworked.support.device.domain.model.DeviceDomain;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for device persistence.
 * Pure interface with no framework dependencies.
 * Infrastructure layer will provide the implementation.
 */
public interface DeviceRepository {
    
    /**
     * Save a device.
     * @param device the device to save
     * @return the saved device
     */
    DeviceDomain save(DeviceDomain device);
    
    /**
     * Find a device by ID.
     * @param id the device ID
     * @return the device if found
     */
    Optional<DeviceDomain> findById(UUID id);
    
    /**
     * Find a device by IP address.
     * @param ipAddress the IP address
     * @return the device if found
     */
    Optional<DeviceDomain> findByIpAddress(String ipAddress);
    
    /**
     * Find a device by MAC address.
     * @param macAddress the MAC address
     * @return the device if found
     */
    Optional<DeviceDomain> findByMacAddress(String macAddress);
    
    /**
     * Find all devices.
     * @return list of all devices
     */
    List<DeviceDomain> findAll();
    
    /**
     * Find devices by site ID.
     * @param siteId the site ID
     * @return list of devices assigned to the site
     */
    List<DeviceDomain> findBySiteId(UUID siteId);
    
    /**
     * Find devices by type.
     * @param deviceType the device type
     * @return list of devices of the specified type
     */
    List<DeviceDomain> findByDeviceType(String deviceType);
    
    /**
     * Check if a device exists by ID.
     * @param id the device ID
     * @return true if exists
     */
    boolean existsById(UUID id);
    
    /**
     * Check if a device exists by IP address.
     * @param ipAddress the IP address
     * @return true if exists
     */
    boolean existsByIpAddress(String ipAddress);
    
    /**
     * Check if a device exists by MAC address.
     * @param macAddress the MAC address
     * @return true if exists
     */
    boolean existsByMacAddress(String macAddress);
    
    /**
     * Delete a device by ID.
     * @param id the device ID
     */
    void deleteById(UUID id);
    
    /**
     * Count all devices.
     * @return the total count
     */
    long count();
    
    /**
     * Count devices by site ID.
     * @param siteId the site ID
     * @return the count of devices in the site
     */
    long countBySiteId(UUID siteId);
}