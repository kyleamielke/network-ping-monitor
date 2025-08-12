package io.thatworked.support.ping.infrastructure.repository;

import io.thatworked.support.ping.domain.AlertState;
import io.thatworked.support.ping.domain.model.AlertStateDomain;
import io.thatworked.support.ping.domain.port.AlertStateRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Infrastructure adapter for alert state repository.
 */
@Component
public class AlertStateRepositoryAdapter implements AlertStateRepository {
    
    private final io.thatworked.support.ping.infrastructure.repository.jpa.AlertStateRepository jpaAlertStateRepository;
    
    public AlertStateRepositoryAdapter(io.thatworked.support.ping.infrastructure.repository.jpa.AlertStateRepository jpaAlertStateRepository) {
        this.jpaAlertStateRepository = jpaAlertStateRepository;
    }
    
    @Override
    public Optional<AlertStateDomain> findById(UUID deviceId) {
        return jpaAlertStateRepository.findById(deviceId)
            .map(this::toDomain);
    }
    
    @Override
    public AlertStateDomain save(AlertStateDomain domain) {
        AlertState entity = toEntity(domain);
        AlertState saved = jpaAlertStateRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public void deleteById(UUID deviceId) {
        jpaAlertStateRepository.deleteById(deviceId);
    }
    
    @Override
    public boolean existsById(UUID deviceId) {
        return jpaAlertStateRepository.existsById(deviceId);
    }
    
    private AlertStateDomain toDomain(AlertState entity) {
        return AlertStateDomain.builder()
            .deviceId(entity.getDeviceId())
            .consecutiveFailures(entity.getConsecutiveFailures())
            .consecutiveSuccesses(entity.getConsecutiveSuccesses())
            .alertActive(entity.isAlerting())
            .lastAlertSent(entity.getLastAlertSent())
            .lastRecoverySent(entity.getLastRecoverySent())
            .lastFailureTime(entity.getLastFailureTime())
            .lastSuccessTime(entity.getLastSuccessTime())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
    
    private AlertState toEntity(AlertStateDomain domain) {
        Instant now = Instant.now();
        
        AlertState entity = new AlertState();
        entity.setDeviceId(domain.getDeviceId());
        entity.setConsecutiveFailures(domain.getConsecutiveFailures());
        entity.setConsecutiveSuccesses(domain.getConsecutiveSuccesses());
        entity.setAlerting(domain.isAlertActive());
        entity.setLastAlertSent(domain.getLastAlertSent());
        entity.setLastRecoverySent(domain.getLastRecoverySent());
        entity.setLastFailureTime(domain.getLastFailureTime());
        entity.setLastSuccessTime(domain.getLastSuccessTime());
        entity.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : now);
        entity.setUpdatedAt(now);
        
        return entity;
    }
}