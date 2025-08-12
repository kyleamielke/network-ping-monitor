package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.infrastructure.dto.DeviceDTO;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DeviceServiceClientFallback implements DeviceServiceClient {
    
    private final StructuredLogger logger;
    
    public DeviceServiceClientFallback(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(DeviceServiceClientFallback.class);
    }
    
    @Override
    public List<DeviceDTO> getAllDevices() {
        logger.with("operation", "getAllDevices")
                .with("fallback", true)
                .warn("Device service unavailable, returning empty device list");
        return Collections.emptyList();
    }
    
    @Override
    public DeviceDTO getDevice(String id) {
        logger.with("operation", "getDevice")
                .with("deviceId", id)
                .with("fallback", true)
                .warn("Device service unavailable, cannot retrieve device");
        return null;
    }
}