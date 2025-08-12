package io.thatworked.support.alert.application.usecase;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.port.AlertQueryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Use case for retrieving alerts.
 * Uses QueryPort for clean architecture separation.
 */
@Service
public class GetAlertsUseCase {
    
    private final AlertQueryPort alertQueryPort;
    
    public GetAlertsUseCase(AlertQueryPort alertQueryPort) {
        this.alertQueryPort = alertQueryPort;
    }
    
    public Optional<AlertDomain> getAlert(UUID alertId) {
        return alertQueryPort.findById(alertId);
    }
    
    public List<AlertDomain> getAlertsByDeviceId(UUID deviceId) {
        return alertQueryPort.findByDeviceId(deviceId);
    }
    
    public List<AlertDomain> getAlertsByDeviceIds(List<UUID> deviceIds) {
        return alertQueryPort.findByDeviceIds(deviceIds);
    }
    
    public List<AlertDomain> getUnresolvedAlertsByDeviceId(UUID deviceId) {
        return alertQueryPort.findUnresolvedByDeviceId(deviceId);
    }
    
    public List<AlertDomain> getUnacknowledgedAlerts() {
        return alertQueryPort.findUnacknowledged();
    }
    
    public List<AlertDomain> getAlertsByType(AlertType type) {
        return alertQueryPort.findByType(type);
    }
    
    public List<AlertDomain> getAlertsByTimestampBetween(Instant start, Instant end) {
        return alertQueryPort.findByTimestampBetween(start, end);
    }
    
    public long countTotal() {
        return alertQueryPort.count();
    }
    
    public long countUnresolved() {
        return alertQueryPort.countUnresolved();
    }
    
    public long countUnacknowledged() {
        return alertQueryPort.countUnacknowledged();
    }
}