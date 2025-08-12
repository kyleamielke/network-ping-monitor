package io.thatworked.support.alert.infrastructure.adapter;

import io.thatworked.support.alert.domain.model.AlertDomain;
import io.thatworked.support.alert.domain.model.AlertType;
import io.thatworked.support.alert.domain.port.AlertRepository;
import io.thatworked.support.alert.infrastructure.entity.Alert;
import io.thatworked.support.alert.infrastructure.mapper.AlertEntityMapper;
import io.thatworked.support.alert.infrastructure.repository.AlertJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter implementing the AlertRepository port.
 * Handles persistence using Spring Data JPA.
 */
@Component
@Transactional
public class AlertRepositoryAdapter implements AlertRepository {
    
    private final AlertJpaRepository jpaRepository;
    private final AlertEntityMapper mapper;
    private final EntityManager entityManager;
    
    public AlertRepositoryAdapter(AlertJpaRepository jpaRepository, AlertEntityMapper mapper, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
    }
    
    @Override
    public AlertDomain save(AlertDomain alertDomain) {
        Alert entity;
        
        // For updates, we need to fetch the existing entity to preserve the version
        if (alertDomain.getId() != null && jpaRepository.existsById(alertDomain.getId())) {
            entity = jpaRepository.findById(alertDomain.getId()).orElseThrow();
            // Update fields from domain
            updateEntityFromDomain(entity, alertDomain);
        } else {
            // For new entities, create from scratch
            entity = mapper.toEntity(alertDomain);
        }
        
        Alert saved = jpaRepository.saveAndFlush(entity);
        // Refresh the entity to get the updated version from the database
        entityManager.refresh(saved);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<AlertDomain> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }
    
    @Override
    public List<AlertDomain> findByDeviceId(UUID deviceId) {
        return jpaRepository.findByDeviceId(deviceId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertDomain> findByDeviceIds(List<UUID> deviceIds) {
        return jpaRepository.findByDeviceIdIn(deviceIds).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertDomain> findUnresolvedByDeviceId(UUID deviceId) {
        return jpaRepository.findByDeviceIdAndIsResolvedFalse(deviceId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertDomain> findUnacknowledged() {
        return jpaRepository.findByIsAcknowledgedFalse().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertDomain> findByType(AlertType type) {
        return jpaRepository.findByAlertType(type).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AlertDomain> findByTimestampBetween(Instant start, Instant end) {
        return jpaRepository.findByTimestampBetween(start, end).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public void deleteByDeviceId(UUID deviceId) {
        jpaRepository.deleteByDeviceId(deviceId);
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public long countUnresolved() {
        return jpaRepository.countByIsResolvedFalse();
    }
    
    @Override
    public long countUnacknowledged() {
        return jpaRepository.countByIsAcknowledgedFalse();
    }
    
    @Override
    public void deleteOlderThan(Instant cutoffDate) {
        jpaRepository.deleteByTimestampBefore(cutoffDate);
    }
    
    private void updateEntityFromDomain(Alert entity, AlertDomain domain) {
        // Update mutable fields from domain
        entity.setMessage(domain.getMessage());
        entity.setResolved(domain.isResolved());
        entity.setResolvedAt(domain.getResolvedAt());
        entity.setAcknowledged(domain.isAcknowledged());
        entity.setAcknowledgedAt(domain.getAcknowledgedAt());
        entity.setAcknowledgedBy(domain.getAcknowledgedBy());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setVersion(domain.getVersion());
        // Note: We don't update immutable fields like id, deviceId, alertType, etc.
    }
}