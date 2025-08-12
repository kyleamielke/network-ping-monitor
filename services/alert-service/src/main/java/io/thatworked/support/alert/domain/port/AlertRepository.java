package io.thatworked.support.alert.domain.port;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for alert persistence operations.
 * This interface defines the contract for storing and retrieving alerts.
 */
public interface AlertRepository {
    
    /**
     * Save an alert to the repository.
     */
    AlertDomain save(AlertDomain alert);
    
    /**
     * Find an alert by its ID.
     */
    Optional<AlertDomain> findById(UUID id);
    
    /**
     * Find all alerts for a specific device.
     */
    List<AlertDomain> findByDeviceId(UUID deviceId);
    
    /**
     * Find all alerts for multiple devices (batch operation).
     */
    List<AlertDomain> findByDeviceIds(List<UUID> deviceIds);
    
    /**
     * Find all unresolved alerts for a specific device.
     */
    List<AlertDomain> findUnresolvedByDeviceId(UUID deviceId);
    
    /**
     * Find all unacknowledged alerts.
     */
    List<AlertDomain> findUnacknowledged();
    
    /**
     * Find all alerts by type.
     */
    List<AlertDomain> findByType(AlertType type);
    
    /**
     * Find alerts created within a time range.
     */
    List<AlertDomain> findByTimestampBetween(Instant start, Instant end);
    
    /**
     * Delete an alert by ID.
     */
    void deleteById(UUID id);
    
    /**
     * Delete all alerts for a specific device.
     */
    void deleteByDeviceId(UUID deviceId);
    
    /**
     * Count total alerts.
     */
    long count();
    
    /**
     * Count unresolved alerts.
     */
    long countUnresolved();
    
    /**
     * Count unacknowledged alerts.
     */
    long countUnacknowledged();
    
    /**
     * Delete alerts older than specified date.
     */
    void deleteOlderThan(Instant cutoffDate);
}