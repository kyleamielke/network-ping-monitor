package io.thatworked.support.report.domain.port;

import java.util.List;
import java.util.UUID;

/**
 * Port for accessing device information from external services.
 */
public interface DeviceDataPort {
    
    /**
     * Retrieves all devices from the device service.
     */
    List<DeviceData> getAllDevices();
    
    /**
     * Retrieves specific devices by their IDs.
     */
    List<DeviceData> getDevices(List<UUID> deviceIds);
    
    /**
     * Retrieves a single device by ID.
     */
    DeviceData getDevice(UUID deviceId);
    
    /**
     * Data transfer object for device information.
     */
    record DeviceData(
        UUID deviceId,
        String name,
        String ipAddress,
        String hostname,
        String type,
        boolean isActive,
        boolean isUp,
        String location
    ) {}
}