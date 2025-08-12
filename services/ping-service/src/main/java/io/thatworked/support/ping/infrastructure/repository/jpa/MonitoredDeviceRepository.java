package io.thatworked.support.ping.infrastructure.repository.jpa;

import io.thatworked.support.ping.domain.MonitoredDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MonitoredDeviceRepository extends JpaRepository<MonitoredDevice, UUID> {
}