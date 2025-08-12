package io.thatworked.support.ping.domain.port;

import io.thatworked.support.ping.domain.model.PingResultDomain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Domain port for ping result persistence operations.
 */
public interface PingResultRepository {
    
    PingResultDomain save(PingResultDomain pingResult);
    
    List<PingResultDomain> findByDeviceId(UUID deviceId);
    
    List<PingResultDomain> findByDeviceIdAndTimestampBetween(UUID deviceId, Instant start, Instant end);
    
    List<PingResultDomain> findRecentByDeviceId(UUID deviceId, int limit);
    
    void deleteByDeviceId(UUID deviceId);
    
    long countByDeviceIdAndTimestampBetween(UUID deviceId, Instant start, Instant end);
    
    long countSuccessfulByDeviceIdAndTimestampBetween(UUID deviceId, Instant start, Instant end);
}