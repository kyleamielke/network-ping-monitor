package io.thatworked.support.ping.infrastructure.repository.jpa;

import io.thatworked.support.ping.domain.PingTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PingTargetRepository extends JpaRepository<PingTarget, UUID> {
    List<PingTarget> findByIsMonitored(boolean isMonitored);

    @Query("SELECT p FROM PingTarget p WHERE p.isMonitored = true")
    List<PingTarget> findAllActiveTargets();

    Optional<PingTarget> findByIpAddress(String ipAddress);

    boolean existsByIpAddress(String ipAddress);
    
    List<PingTarget> findByDeviceIdIn(List<UUID> deviceIds);
}