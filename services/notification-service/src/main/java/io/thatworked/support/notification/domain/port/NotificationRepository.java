package io.thatworked.support.notification.domain.port;

import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.model.NotificationResult;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain port for notification persistence.
 * Pure interface with no framework dependencies.
 */
public interface NotificationRepository {
    
    /**
     * Save a notification request.
     */
    NotificationRequest saveRequest(NotificationRequest request);
    
    /**
     * Save a notification result.
     */
    NotificationResult saveResult(NotificationResult result);
    
    /**
     * Find a notification request by ID.
     */
    Optional<NotificationRequest> findRequestById(UUID id);
    
    /**
     * Find notification results for a request.
     */
    List<NotificationResult> findResultsByRequestId(UUID requestId);
    
    /**
     * Find notification requests by source event ID.
     */
    List<NotificationRequest> findRequestsBySourceEventId(UUID sourceEventId);
    
    /**
     * Find notification requests within a time range.
     */
    List<NotificationRequest> findRequestsByTimeRange(Instant startTime, Instant endTime);
    
    /**
     * Find failed notifications within a time range.
     */
    List<NotificationRequest> findFailedRequestsByTimeRange(Instant startTime, Instant endTime);
}