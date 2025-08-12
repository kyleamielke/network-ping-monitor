package io.thatworked.support.notification.infrastructure.repository;

import io.thatworked.support.notification.infrastructure.entity.NotificationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for notification requests.
 */
@Repository
public interface JpaNotificationRequestRepository extends JpaRepository<NotificationRequestEntity, UUID> {
    
    List<NotificationRequestEntity> findBySourceEventId(UUID sourceEventId);
    
    @Query("SELECT n FROM NotificationRequestEntity n WHERE n.requestedAt >= :startTime AND n.requestedAt <= :endTime")
    List<NotificationRequestEntity> findByTimeRange(@Param("startTime") Instant startTime, 
                                                   @Param("endTime") Instant endTime);
    
    @Query("SELECT n FROM NotificationRequestEntity n WHERE n.requestedAt >= :startTime AND n.requestedAt <= :endTime " +
           "AND EXISTS (SELECT r FROM NotificationResultEntity r WHERE r.notificationRequestId = n.id AND r.isSuccessful = false)")
    List<NotificationRequestEntity> findFailedByTimeRange(@Param("startTime") Instant startTime,
                                                         @Param("endTime") Instant endTime);
}