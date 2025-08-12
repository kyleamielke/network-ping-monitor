package io.thatworked.support.ping.infrastructure.repository;

import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.domain.PingStatus;
import io.thatworked.support.ping.domain.model.PingResultDomain;
import io.thatworked.support.ping.domain.port.PingResultRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

/**
 * Infrastructure adapter for ping result repository.
 */
@Component
public class PingResultRepositoryAdapter implements PingResultRepository {
    
    private final io.thatworked.support.ping.infrastructure.repository.jpa.PingResultRepository jpaPingResultRepository;
    
    public PingResultRepositoryAdapter(io.thatworked.support.ping.infrastructure.repository.jpa.PingResultRepository jpaPingResultRepository) {
        this.jpaPingResultRepository = jpaPingResultRepository;
    }
    
    @Override
    public PingResultDomain save(PingResultDomain domain) {
        PingResult entity = toEntity(domain);
        PingResult saved = jpaPingResultRepository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public List<PingResultDomain> findByDeviceId(UUID deviceId) {
        return jpaPingResultRepository.findLatestByDeviceId(deviceId, Integer.MAX_VALUE).stream()
            .map(this::toDomain)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<PingResultDomain> findByDeviceIdAndTimestampBetween(UUID deviceId, Instant start, Instant end) {
        return jpaPingResultRepository.findByDeviceIdAndTimeBetween(deviceId, start, end).stream()
            .map(this::toDomain)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<PingResultDomain> findRecentByDeviceId(UUID deviceId, int limit) {
        return jpaPingResultRepository.findLatestByDeviceId(deviceId, limit).stream()
            .map(this::toDomain)
            .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public void deleteByDeviceId(UUID deviceId) {
        // Find all results for device and delete them
        var results = jpaPingResultRepository.findLatestByDeviceId(deviceId, Integer.MAX_VALUE);
        jpaPingResultRepository.deleteAll(results);
    }
    
    @Override
    public long countByDeviceIdAndTimestampBetween(UUID deviceId, Instant start, Instant end) {
        return jpaPingResultRepository.findByDeviceIdAndTimeBetween(deviceId, start, end).size();
    }
    
    @Override
    public long countSuccessfulByDeviceIdAndTimestampBetween(UUID deviceId, Instant start, Instant end) {
        return jpaPingResultRepository.findByDeviceIdAndTimeBetween(deviceId, start, end).stream()
            .filter(result -> result.getStatus().isSuccess())
            .count();
    }
    
    private PingResultDomain toDomain(PingResult entity) {
        return new PingResultDomain(
            entity.getDeviceId(),
            null, // IP address not stored in entity
            entity.getStatus().isSuccess(),
            entity.getRoundTripTime(),
            null, // Error message not stored in entity
            entity.getTime()
        );
    }
    
    private PingResult toEntity(PingResultDomain domain) {
        return PingResult.builder()
            .time(domain.getTimestamp().atZone(ZoneId.systemDefault()).toInstant())
            .deviceId(domain.getDeviceId())
            .roundTripTime(domain.getResponseTime() != null ? domain.getResponseTime().doubleValue() : null)
            .status(domain.isSuccess() ? PingStatus.SUCCESS : PingStatus.FAILURE)
            .build();
    }
}