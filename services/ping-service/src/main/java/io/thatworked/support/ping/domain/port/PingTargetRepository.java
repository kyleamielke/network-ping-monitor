package io.thatworked.support.ping.domain.port;

import io.thatworked.support.ping.domain.model.PingTargetDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for ping target persistence operations.
 */
public interface PingTargetRepository {
    
    Optional<PingTargetDomain> findById(UUID deviceId);
    
    List<PingTargetDomain> findAll();
    
    List<PingTargetDomain> findAllActive();
    
    PingTargetDomain save(PingTargetDomain pingTarget);
    
    void deleteById(UUID deviceId);
    
    boolean existsById(UUID deviceId);
    
    List<PingTargetDomain> findByMonitored(boolean monitored);
    
    List<PingTargetDomain> findByDeviceIds(List<UUID> deviceIds);
}