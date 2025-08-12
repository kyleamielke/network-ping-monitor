package io.thatworked.support.alert.application.service;

import io.thatworked.support.alert.application.usecase.GetAlertsUseCase;
import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.service.AlertDomainService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service for querying alerts.
 * Handles read operations using clean architecture UseCase pattern.
 */
@Service
@Transactional(readOnly = true)
public class AlertQueryApplicationService {
    
    private final StructuredLogger logger;
    private final GetAlertsUseCase getAlertsUseCase;
    private final AlertDomainService alertDomainService;
    
    public AlertQueryApplicationService(StructuredLoggerFactory loggerFactory, 
                                       GetAlertsUseCase getAlertsUseCase,
                                       AlertDomainService alertDomainService) {
        this.logger = loggerFactory.getLogger(AlertQueryApplicationService.class);
        this.getAlertsUseCase = getAlertsUseCase;
        this.alertDomainService = alertDomainService;
    }
    
    public Optional<AlertDomain> findById(UUID alertId) {
        return getAlertsUseCase.getAlert(alertId);
    }
    
    public List<AlertDomain> findByDeviceId(UUID deviceId) {
        return getAlertsUseCase.getAlertsByDeviceId(deviceId);
    }
    
    public List<AlertDomain> findByDeviceIds(List<UUID> deviceIds) {
        return getAlertsUseCase.getAlertsByDeviceIds(deviceIds);
    }
    
    public List<AlertDomain> findUnresolvedByDeviceId(UUID deviceId) {
        return getAlertsUseCase.getUnresolvedAlertsByDeviceId(deviceId);
    }
    
    public List<AlertDomain> findUnacknowledged() {
        return getAlertsUseCase.getUnacknowledgedAlerts();
    }
    
    public List<AlertDomain> findByType(AlertType type) {
        return getAlertsUseCase.getAlertsByType(type);
    }
    
    public List<AlertDomain> findByTimestampBetween(Instant start, Instant end) {
        return getAlertsUseCase.getAlertsByTimestampBetween(start, end);
    }
    
    public AlertDomainService.AlertStatistics getStatistics() {
        return alertDomainService.getStatistics();
    }
    
    public long countTotal() {
        return getAlertsUseCase.countTotal();
    }
    
    public long countUnresolved() {
        return getAlertsUseCase.countUnresolved();
    }
    
    public long countUnacknowledged() {
        return getAlertsUseCase.countUnacknowledged();
    }
}