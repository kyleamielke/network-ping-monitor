package io.thatworked.support.notification.application.usecase;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.application.dto.NotificationHistoryQuery;
import io.thatworked.support.notification.application.dto.NotificationHistoryResponse;
import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.service.NotificationDomainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Use case for retrieving notification history.
 * Translates between application DTOs and domain objects.
 */
@Service
public class GetNotificationHistoryUseCase {
    
    private final StructuredLogger logger;
    private final NotificationDomainService domainService;
    
    public GetNotificationHistoryUseCase(StructuredLoggerFactory loggerFactory,
                                         NotificationDomainService domainService) {
        this.logger = loggerFactory.getLogger(GetNotificationHistoryUseCase.class);
        this.domainService = domainService;
    }
    
    public List<NotificationHistoryResponse> execute(NotificationHistoryQuery query) {
        logger.with("operation", "getNotificationHistory")
                .with("startTime", query.getStartTime())
                .with("endTime", query.getEndTime())
                .with("failedOnly", query.isIncludeFailedOnly())
                .debug("Executing get notification history use case");
        
        try {
            List<NotificationRequest> requests;
            
            if (query.isIncludeFailedOnly()) {
                requests = domainService.getFailedNotifications(query.getStartTime(), query.getEndTime());
            } else {
                requests = domainService.getNotificationHistory(query.getStartTime(), query.getEndTime());
            }
            
            List<NotificationHistoryResponse> responses = requests.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
            
            logger.with("operation", "getNotificationHistory")
                    .with("count", responses.size())
                    .info("Get notification history use case completed");
            
            return responses;
        } catch (Exception e) {
            logger.with("operation", "getNotificationHistory")
                    .with("error", e.getMessage())
                    .with("errorType", e.getClass().getSimpleName())
                    .error("Failed to execute get notification history use case", e);
            throw e;
        }
    }
    
    private NotificationHistoryResponse toResponse(NotificationRequest request) {
        return new NotificationHistoryResponse(
            request.getId(),
            request.getType().name(),
            request.getChannel().name(),
            request.getRecipient(),
            request.getSubject(),
            request.getMessage(),
            request.getMetadata(),
            request.getRequestedAt(),
            request.getSourceEventId()
        );
    }
}