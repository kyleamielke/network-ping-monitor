package io.thatworked.support.ping.infrastructure.repository;

import io.thatworked.support.ping.domain.PingTarget;
import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.port.PingTargetRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter for ping target repository.
 */
@Component
public class PingTargetRepositoryAdapter implements PingTargetRepository {
    
    private final io.thatworked.support.ping.infrastructure.repository.jpa.PingTargetRepository jpaPingTargetRepository;
    
    public PingTargetRepositoryAdapter(io.thatworked.support.ping.infrastructure.repository.jpa.PingTargetRepository jpaPingTargetRepository) {
        this.jpaPingTargetRepository = jpaPingTargetRepository;
    }
    
    @Override
    public Optional<PingTargetDomain> findById(UUID deviceId) {
        return jpaPingTargetRepository.findById(deviceId)
            .map(this::toDomain);
    }
    
    @Override
    public List<PingTargetDomain> findAll() {
        return jpaPingTargetRepository.findAll().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PingTargetDomain> findAllActive() {
        return jpaPingTargetRepository.findAllActiveTargets().stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public PingTargetDomain save(PingTargetDomain domain) {
        // Check if entity exists to avoid duplicate key issues
        Optional<PingTarget> existing = jpaPingTargetRepository.findById(domain.getDeviceId());
        
        PingTarget entity;
        if (existing.isPresent()) {
            // Update existing entity
            entity = existing.get();
            entity.setIpAddress(domain.getIpAddress());
            entity.setHostname(domain.getHostname());
            entity.setMonitored(domain.isMonitored());
            entity.setPingIntervalSeconds(domain.getPingIntervalSeconds());
            // Timestamps are managed by JPA annotations
        } else {
            // Create new entity
            entity = toEntity(domain);
        }
        
        PingTarget saved = jpaPingTargetRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public void deleteById(UUID deviceId) {
        jpaPingTargetRepository.deleteById(deviceId);
    }
    
    @Override
    public boolean existsById(UUID deviceId) {
        return jpaPingTargetRepository.existsById(deviceId);
    }
    
    @Override
    public List<PingTargetDomain> findByMonitored(boolean monitored) {
        return jpaPingTargetRepository.findByIsMonitored(monitored).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<PingTargetDomain> findByDeviceIds(List<UUID> deviceIds) {
        return jpaPingTargetRepository.findByDeviceIdIn(deviceIds).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
    }
    
    private PingTargetDomain toDomain(PingTarget entity) {
        return PingTargetDomain.builder()
            .deviceId(entity.getDeviceId())
            .ipAddress(entity.getIpAddress())
            .hostname(entity.getHostname())
            .monitored(entity.isMonitored())
            .pingIntervalSeconds(entity.getPingIntervalSeconds())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
    
    private PingTarget toEntity(PingTargetDomain domain) {
        Instant now = Instant.now();
        
        return PingTarget.builder()
            .deviceId(domain.getDeviceId())
            .ipAddress(domain.getIpAddress())
            .hostname(domain.getHostname())
            .isMonitored(domain.isMonitored())
            .pingIntervalSeconds(domain.getPingIntervalSeconds())
            .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : now)
            .updatedAt(now)
            // Don't set version - let JPA handle it automatically
            .build();
    }
}