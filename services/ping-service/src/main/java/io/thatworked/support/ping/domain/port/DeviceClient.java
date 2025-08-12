package io.thatworked.support.ping.domain.port;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for device information.
 * This will be implemented by infrastructure adapters.
 */
public interface DeviceClient {
    
    Optional<DeviceInfo> findById(UUID deviceId);
    
    record DeviceInfo(UUID id, String name, String ipAddress) {}
}