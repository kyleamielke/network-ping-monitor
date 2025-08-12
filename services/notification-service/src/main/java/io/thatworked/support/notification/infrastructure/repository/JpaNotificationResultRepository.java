package io.thatworked.support.notification.infrastructure.repository;

import io.thatworked.support.notification.infrastructure.entity.NotificationResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data JPA repository for notification results.
 */
@Repository
public interface JpaNotificationResultRepository extends JpaRepository<NotificationResultEntity, UUID> {
    
    List<NotificationResultEntity> findByNotificationRequestId(UUID notificationRequestId);
}