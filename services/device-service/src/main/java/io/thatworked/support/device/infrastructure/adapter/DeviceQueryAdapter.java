package io.thatworked.support.device.infrastructure.adapter;

import io.thatworked.support.device.api.mapper.DeviceMapper;
import io.thatworked.support.device.domain.model.DeviceDomain;
import io.thatworked.support.device.domain.port.DeviceQueryPort;
import io.thatworked.support.device.infrastructure.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter implementing domain query port.
 * Bridges domain layer with JPA repositories.
 */
@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceQueryAdapter implements DeviceQueryPort {
    
    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;
    
    @Override
    public Optional<DeviceDomain> findByUuid(UUID uuid) {
        return deviceRepository.findById(uuid)
            .map(deviceMapper::toDomain);
    }
    
    @Override
    public List<DeviceDomain> findByUuids(List<UUID> uuids) {
        return deviceRepository.findAllById(uuids).stream()
            .map(deviceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceDomain> findAll() {
        return deviceRepository.findAll().stream()
            .map(deviceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceDomain> findByType(String deviceType) {
        return deviceRepository.findByDeviceType(deviceType).stream()
            .map(deviceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceDomain> findBySite(UUID siteId) {
        return deviceRepository.findBySiteId(siteId).stream()
            .map(deviceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceDomain> findBySiteAndType(UUID siteId, String deviceType) {
        return deviceRepository.findBySiteIdAndDeviceType(siteId, deviceType).stream()
            .map(deviceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<DeviceDomain> findUnassigned() {
        return deviceRepository.findBySiteIdIsNull().stream()
            .map(deviceMapper::toDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return deviceRepository.count();
    }
    
    @Override
    public long countByType(String deviceType) {
        return deviceRepository.countByType(deviceType);
    }
    
    @Override
    public long countBySite(UUID siteId) {
        return deviceRepository.countBySiteId(siteId);
    }
    
    @Override
    public boolean existsByUuid(UUID uuid) {
        return deviceRepository.existsById(uuid);
    }
    
    @Override
    public boolean existsByIpAddress(String ipAddress) {
        return deviceRepository.existsByIpAddress(ipAddress);
    }
}