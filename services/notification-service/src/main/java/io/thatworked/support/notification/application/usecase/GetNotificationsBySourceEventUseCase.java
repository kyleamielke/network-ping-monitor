package io.thatworked.support.notification.application.usecase;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.notification.application.dto.NotificationHistoryResponse;
import io.thatworked.support.notification.domain.model.NotificationRequest;
import io.thatworked.support.notification.domain.service.NotificationDomainService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Use case for retrieving notifications by source event.
 * Translates between application DTOs and domain objects.
 */
@Service
public class GetNotificationsBySourceEventUseCase {
    
    private final StructuredLogger logger;
    private final NotificationDomainService domainService;
    
    public GetNotificationsBySourceEventUseCase(StructuredLoggerFactory loggerFactory,
                                                NotificationDomainService domainService) {
        this.logger = loggerFactory.getLogger(GetNotificationsBySourceEventUseCase.class);
        this.domainService = domainService;
    }
    
    public List<NotificationHistoryResponse> execute(UUID sourceEventId) {
        logger.with("operation", "getNotificationsBySourceEvent")
                .with("sourceEventId", sourceEventId)
                .debug("Executing get notifications by source event use case");
        
        try {
            List<NotificationRequest> requests = domainService.getNotificationsBySourceEvent(sourceEventId);
            
            List<NotificationHistoryResponse> responses = requests.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
            
            logger.with("operation", "getNotificationsBySourceEvent")
                    .with("sourceEventId", sourceEventId)
                    .with("count", responses.size())
                    .info("Get notifications by source event use case completed");
            
            return responses;
        } catch (Exception e) {
            logger.with("operation", "getNotificationsBySourceEvent")
                    .with("sourceEventId", sourceEventId)
                    .with("error", e.getMessage())
                    .with("errorType", e.getClass().getSimpleName())
                    .error("Failed to execute get notifications by source event use case", e);
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