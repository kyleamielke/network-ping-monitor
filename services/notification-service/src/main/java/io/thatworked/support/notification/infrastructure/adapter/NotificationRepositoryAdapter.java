package io.thatworked.support.notification.infrastructure.adapter;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.domain.exception.NotificationRepositoryException;
import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.model.NotificationResult;
import io.thatworked.support.notification.domain.port.NotificationRepository;
import io.thatworked.support.notification.infrastructure.entity.NotificationRequestEntity;
import io.thatworked.support.notification.infrastructure.entity.NotificationResultEntity;
import io.thatworked.support.notification.infrastructure.mapper.NotificationMapper;
import io.thatworked.support.notification.infrastructure.repository.JpaNotificationRequestRepository;
import io.thatworked.support.notification.infrastructure.repository.JpaNotificationResultRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of NotificationRepository using JPA.
 */
@Component
@Transactional
public class NotificationRepositoryAdapter implements NotificationRepository {
    
    private final StructuredLogger logger;
    private final JpaNotificationRequestRepository requestRepository;
    private final JpaNotificationResultRepository resultRepository;
    private final NotificationMapper mapper;
    
    public NotificationRepositoryAdapter(StructuredLoggerFactory loggerFactory,
                                       JpaNotificationRequestRepository requestRepository,
                                       JpaNotificationResultRepository resultRepository,
                                       NotificationMapper mapper) {
        this.logger = loggerFactory.getLogger(NotificationRepositoryAdapter.class);
        this.requestRepository = requestRepository;
        this.resultRepository = resultRepository;
        this.mapper = mapper;
    }
    
    @Override
    public NotificationRequest saveRequest(NotificationRequest request) {
        try {
            NotificationRequestEntity entity = mapper.toEntity(request);
            NotificationRequestEntity saved = requestRepository.save(entity);
            
            logger.with("operation", "saveNotificationRequest")
                    .with("notificationId", saved.getId())
                    .debug("Saved notification request");
            
            return mapper.toDomain(saved);
        } catch (DataAccessException e) {
            logger.with("operation", "saveNotificationRequest")
                    .with("notificationType", request.getType())
                    .with("error", e.getMessage())
                    .error("Failed to save notification request", e);
            throw new NotificationRepositoryException("Failed to save notification request", e);
        }
    }
    
    @Override
    public NotificationResult saveResult(NotificationResult result) {
        try {
            NotificationResultEntity entity = mapper.toEntity(result);
            NotificationResultEntity saved = resultRepository.save(entity);
            
            logger.with("operation", "saveNotificationResult")
                    .with("resultId", saved.getId())
                    .with("notificationRequestId", saved.getNotificationRequestId())
                    .with("successful", saved.isSuccessful())
                    .debug("Saved notification result");
            
            return mapper.toDomain(saved);
        } catch (DataAccessException e) {
            logger.with("operation", "saveNotificationResult")
                    .with("notificationRequestId", result.getNotificationRequestId())
                    .with("error", e.getMessage())
                    .error("Failed to save notification result", e);
            throw new NotificationRepositoryException("Failed to save notification result", e);
        }
    }
    
    @Override
    public Optional<NotificationRequest> findRequestById(UUID id) {
        try {
            return requestRepository.findById(id)
                .map(mapper::toDomain);
        } catch (DataAccessException e) {
            logger.with("operation", "findRequestById")
                    .with("notificationId", id)
                    .with("error", e.getMessage())
                    .error("Failed to find notification request by ID", e);
            throw new NotificationRepositoryException("Failed to find notification request by ID: " + id, e);
        }
    }
    
    @Override
    public List<NotificationResult> findResultsByRequestId(UUID requestId) {
        try {
            return resultRepository.findByNotificationRequestId(requestId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            logger.with("operation", "findResultsByRequestId")
                    .with("requestId", requestId)
                    .with("error", e.getMessage())
                    .error("Failed to find notification results by request ID", e);
            throw new NotificationRepositoryException("Failed to find notification results by request ID: " + requestId, e);
        }
    }
    
    @Override
    public List<NotificationRequest> findRequestsBySourceEventId(UUID sourceEventId) {
        try {
            List<NotificationRequest> requests = requestRepository.findBySourceEventId(sourceEventId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
            
            logger.with("operation", "findRequestsBySourceEventId")
                    .with("sourceEventId", sourceEventId)
                    .with("count", requests.size())
                    .debug("Found notification requests by source event ID");
            
            return requests;
        } catch (DataAccessException e) {
            logger.with("operation", "findRequestsBySourceEventId")
                    .with("sourceEventId", sourceEventId)
                    .with("error", e.getMessage())
                    .error("Failed to find notification requests by source event ID", e);
            throw new NotificationRepositoryException("Failed to find notification requests by source event ID: " + sourceEventId, e);
        }
    }
    
    @Override
    public List<NotificationRequest> findRequestsByTimeRange(Instant startTime, Instant endTime) {
        try {
            List<NotificationRequest> requests = requestRepository.findByTimeRange(startTime, endTime).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
            
            logger.with("operation", "findRequestsByTimeRange")
                    .with("startTime", startTime)
                    .with("endTime", endTime)
                    .with("count", requests.size())
                    .debug("Found notification requests by time range");
            
            return requests;
        } catch (DataAccessException e) {
            logger.with("operation", "findRequestsByTimeRange")
                    .with("startTime", startTime)
                    .with("endTime", endTime)
                    .with("error", e.getMessage())
                    .error("Failed to find notification requests by time range", e);
            throw new NotificationRepositoryException("Failed to find notification requests by time range", e);
        }
    }
    
    @Override
    public List<NotificationRequest> findFailedRequestsByTimeRange(Instant startTime, Instant endTime) {
        try {
            List<NotificationRequest> requests = requestRepository.findFailedByTimeRange(startTime, endTime).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
            
            logger.with("operation", "findFailedRequestsByTimeRange")
                    .with("startTime", startTime)
                    .with("endTime", endTime)
                    .with("count", requests.size())
                    .debug("Found failed notification requests by time range");
            
            return requests;
        } catch (DataAccessException e) {
            logger.with("operation", "findFailedRequestsByTimeRange")
                    .with("startTime", startTime)
                    .with("endTime", endTime)
                    .with("error", e.getMessage())
                    .error("Failed to find failed notification requests by time range", e);
            throw new NotificationRepositoryException("Failed to find failed notification requests by time range", e);
        }
    }
}