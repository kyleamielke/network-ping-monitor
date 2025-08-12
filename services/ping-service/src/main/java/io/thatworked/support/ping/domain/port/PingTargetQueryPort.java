package io.thatworked.support.ping.domain.port;

import io.thatworked.support.ping.domain.model.PingTargetDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for ping target query operations.
 * Follows clean architecture principles by separating query concerns.
 */
public interface PingTargetQueryPort {
    
    Optional<PingTargetDomain> findById(UUID deviceId);
    
    List<PingTargetDomain> findAll();
    
    List<PingTargetDomain> findAllActive();
    
    List<PingTargetDomain> findByMonitored(boolean monitored);
    
    List<PingTargetDomain> findByDeviceIds(List<UUID> deviceIds);
    
    boolean existsById(UUID deviceId);
}