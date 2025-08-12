package io.thatworked.support.ping.application.service;

import io.thatworked.support.ping.domain.MonitoredDevice;
import io.thatworked.support.ping.infrastructure.repository.jpa.MonitoredDeviceRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class MonitoredDeviceService {
    
    private final StructuredLogger logger;
    private final MonitoredDeviceRepository repository;
    
    public MonitoredDeviceService(StructuredLoggerFactory loggerFactory,
                                  MonitoredDeviceRepository repository) {
        this.logger = loggerFactory.getLogger(MonitoredDeviceService.class);
        this.repository = repository;
    }
    
    @Transactional(readOnly = true)
    public Optional<MonitoredDevice> findById(UUID deviceId) {
        logger.with("deviceId", deviceId).debug("Finding monitored device by ID");
        return repository.findById(deviceId);
    }
    
    @Transactional
    public MonitoredDevice save(MonitoredDevice device) {
        logger.with("deviceId", device.getDeviceId())
              .with("deviceName", device.getDeviceName())
              .with("ipAddress", device.getIpAddress())
              .debug("Saving monitored device");
        return repository.save(device);
    }
    
    @Transactional
    public void deleteById(UUID deviceId) {
        logger.with("deviceId", deviceId).debug("Deleting monitored device");
        repository.deleteById(deviceId);
    }
    
    @Transactional(readOnly = true)
    public boolean existsById(UUID deviceId) {
        return repository.existsById(deviceId);
    }
}