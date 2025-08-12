package io.thatworked.support.ping.infrastructure.repository.jpa;

import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.domain.PingResultId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface PingResultRepository extends JpaRepository<PingResult, PingResultId> {
    @Query(value = "SELECT * FROM ping_results WHERE device_id = :deviceId ORDER BY time DESC LIMIT :limit",
           nativeQuery = true)
    List<PingResult> findLatestByDeviceId(@Param("deviceId") UUID deviceId, @Param("limit") int limit);

    @Query(value = "SELECT * FROM ping_results WHERE device_id = :deviceId AND time > :since ORDER BY time DESC",
           nativeQuery = true)
    List<PingResult> findByDeviceIdSince(@Param("deviceId") UUID deviceId, @Param("since") Instant since);

    @Query(value = "SELECT AVG(round_trip_time) FROM ping_results WHERE device_id = :deviceId AND time > :since AND status = 'SUCCESS'",
           nativeQuery = true)
    Double getAverageRtt(@Param("deviceId") UUID deviceId, @Param("since") Instant since);

    @Query(value = "SELECT COUNT(*)::numeric / (SELECT CASE WHEN COUNT(*) = 0 THEN 1 ELSE COUNT(*) END FROM ping_results WHERE device_id = :deviceId AND time > :since) " +
           "FROM ping_results WHERE device_id = :deviceId AND time > :since AND status = 'SUCCESS'",
           nativeQuery = true)
    Double getSuccessRate(@Param("deviceId") UUID deviceId, @Param("since") Instant since);

    @Query(value = "SELECT * FROM ping_results WHERE device_id = :deviceId AND time >= :startTime AND time <= :endTime ORDER BY time DESC",
           nativeQuery = true)
    List<PingResult> findByDeviceIdAndTimeBetween(@Param("deviceId") UUID deviceId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
}