package io.thatworked.support.alert.infrastructure.repository;

import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.infrastructure.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for Alert entities.
 */
@Repository
public interface AlertJpaRepository extends JpaRepository<Alert, UUID> {
    
    List<Alert> findByDeviceId(UUID deviceId);
    
    List<Alert> findByDeviceIdIn(List<UUID> deviceIds);
    
    List<Alert> findByDeviceIdAndIsResolvedFalse(UUID deviceId);
    
    List<Alert> findByIsAcknowledgedFalse();
    
    List<Alert> findByAlertType(AlertType alertType);
    
    List<Alert> findByTimestampBetween(Instant start, Instant end);
    
    List<Alert> findByDeviceIdOrderByTimestampDesc(UUID deviceId);
    
    List<Alert> findByDeviceIdAndIsResolvedFalseOrderByTimestampDesc(UUID deviceId);
    
    List<Alert> findByDeviceIdAndAlertTypeOrderByTimestampDesc(UUID deviceId, AlertType alertType);
    
    List<Alert> findByDeviceIdAndAlertTypeAndIsResolvedFalseOrderByTimestampDesc(UUID deviceId, AlertType alertType);
    
    @Query("SELECT a FROM Alert a WHERE a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<Alert> findRecentAlerts(@Param("since") Instant since);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.timestamp >= :since")
    long countRecentAlerts(@Param("since") Instant since);
    
    @Query("SELECT a FROM Alert a WHERE " +
           "(:deviceId IS NULL OR a.deviceId = :deviceId) AND " +
           "(:alertType IS NULL OR a.alertType = :alertType) AND " +
           "(:resolved IS NULL OR a.isResolved = :resolved) AND " +
           "(:acknowledged IS NULL OR a.isAcknowledged = :acknowledged) AND " +
           "(:startTime IS NULL OR a.timestamp >= :startTime) AND " +
           "(:endTime IS NULL OR a.timestamp <= :endTime)")
    Page<Alert> findAlertsWithFilters(@Param("deviceId") UUID deviceId,
                                      @Param("alertType") AlertType alertType,
                                      @Param("resolved") Boolean resolved,
                                      @Param("acknowledged") Boolean acknowledged,
                                      @Param("startTime") Instant startTime,
                                      @Param("endTime") Instant endTime,
                                      Pageable pageable);
    
    @Modifying
    @Query("DELETE FROM Alert a WHERE a.deviceId = :deviceId")
    void deleteByDeviceId(@Param("deviceId") UUID deviceId);
    
    @Modifying
    @Query("DELETE FROM Alert a WHERE a.timestamp < :cutoffDate")
    void deleteByTimestampBefore(@Param("cutoffDate") Instant cutoffDate);
    
    @Modifying
    @Query("DELETE FROM Alert a WHERE a.isResolved = true AND a.updatedAt < :cutoffTime")
    void deleteOldResolvedAlerts(@Param("cutoffTime") Instant cutoffTime);
    
    long countByIsResolvedFalse();
    
    long countByIsAcknowledgedFalse();
}