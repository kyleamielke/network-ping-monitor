package io.thatworked.support.alert.domain.port;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for alert query operations.
 * Follows clean architecture principles by separating query concerns.
 */
public interface AlertQueryPort {
    
    Optional<AlertDomain> findById(UUID alertId);
    
    List<AlertDomain> findByDeviceId(UUID deviceId);
    
    List<AlertDomain> findByDeviceIds(List<UUID> deviceIds);
    
    List<AlertDomain> findUnresolvedByDeviceId(UUID deviceId);
    
    List<AlertDomain> findUnacknowledged();
    
    List<AlertDomain> findByType(AlertType type);
    
    List<AlertDomain> findByTimestampBetween(Instant start, Instant end);
    
    long count();
    
    long countUnresolved();
    
    long countUnacknowledged();
}