package io.thatworked.support.alert.infrastructure.adapter;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.port.AlertQueryPort;
import io.thatworked.support.alert.domain.port.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Infrastructure adapter implementing domain query port.
 * Bridges domain layer with repository implementations.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertQueryAdapter implements AlertQueryPort {
    
    private final AlertRepository alertRepository;
    
    @Override
    public Optional<AlertDomain> findById(UUID alertId) {
        return alertRepository.findById(alertId);
    }
    
    @Override
    public List<AlertDomain> findByDeviceId(UUID deviceId) {
        return alertRepository.findByDeviceId(deviceId);
    }
    
    @Override
    public List<AlertDomain> findByDeviceIds(List<UUID> deviceIds) {
        return alertRepository.findByDeviceIds(deviceIds);
    }
    
    @Override
    public List<AlertDomain> findUnresolvedByDeviceId(UUID deviceId) {
        return alertRepository.findUnresolvedByDeviceId(deviceId);
    }
    
    @Override
    public List<AlertDomain> findUnacknowledged() {
        return alertRepository.findUnacknowledged();
    }
    
    @Override
    public List<AlertDomain> findByType(AlertType type) {
        return alertRepository.findByType(type);
    }
    
    @Override
    public List<AlertDomain> findByTimestampBetween(Instant start, Instant end) {
        return alertRepository.findByTimestampBetween(start, end);
    }
    
    @Override
    public long count() {
        return alertRepository.count();
    }
    
    @Override
    public long countUnresolved() {
        return alertRepository.countUnresolved();
    }
    
    @Override
    public long countUnacknowledged() {
        return alertRepository.countUnacknowledged();
    }
}