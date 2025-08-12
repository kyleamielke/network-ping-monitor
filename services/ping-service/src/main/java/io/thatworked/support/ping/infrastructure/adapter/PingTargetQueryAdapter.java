package io.thatworked.support.ping.infrastructure.adapter;

import io.thatworked.support.ping.domain.model.PingTargetDomain;
import io.thatworked.support.ping.domain.port.PingTargetQueryPort;
import io.thatworked.support.ping.domain.port.PingTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
public class PingTargetQueryAdapter implements PingTargetQueryPort {
    
    private final PingTargetRepository pingTargetRepository;
    
    @Override
    public Optional<PingTargetDomain> findById(UUID deviceId) {
        return pingTargetRepository.findById(deviceId);
    }
    
    @Override
    public List<PingTargetDomain> findAll() {
        return pingTargetRepository.findAll();
    }
    
    @Override
    public List<PingTargetDomain> findAllActive() {
        return pingTargetRepository.findAllActive();
    }
    
    @Override
    public List<PingTargetDomain> findByMonitored(boolean monitored) {
        return pingTargetRepository.findByMonitored(monitored);
    }
    
    @Override
    public List<PingTargetDomain> findByDeviceIds(List<UUID> deviceIds) {
        return pingTargetRepository.findByDeviceIds(deviceIds);
    }
    
    @Override
    public boolean existsById(UUID deviceId) {
        return pingTargetRepository.existsById(deviceId);
    }
}