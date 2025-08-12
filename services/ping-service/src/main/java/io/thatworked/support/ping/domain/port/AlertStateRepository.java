package io.thatworked.support.ping.domain.port;

import io.thatworked.support.ping.domain.model.AlertStateDomain;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for alert state persistence operations.
 */
public interface AlertStateRepository {
    
    Optional<AlertStateDomain> findById(UUID deviceId);
    
    AlertStateDomain save(AlertStateDomain alertState);
    
    void deleteById(UUID deviceId);
    
    boolean existsById(UUID deviceId);
}