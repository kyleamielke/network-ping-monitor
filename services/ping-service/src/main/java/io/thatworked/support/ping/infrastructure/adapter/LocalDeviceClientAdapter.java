package io.thatworked.support.ping.infrastructure.adapter;

import io.thatworked.support.ping.application.service.MonitoredDeviceService;
import io.thatworked.support.ping.domain.MonitoredDevice;
import io.thatworked.support.ping.domain.port.DeviceClient;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Local implementation of DeviceClient that uses MonitoredDevice data
 * instead of making REST calls to device-service.
 */
@Component
public class LocalDeviceClientAdapter implements DeviceClient {
    
    private final MonitoredDeviceService monitoredDeviceService;
    
    public LocalDeviceClientAdapter(MonitoredDeviceService monitoredDeviceService) {
        this.monitoredDeviceService = monitoredDeviceService;
    }
    
    @Override
    public Optional<DeviceInfo> findById(UUID deviceId) {
        return monitoredDeviceService.findById(deviceId)
            .map(this::toDeviceInfo);
    }
    
    private DeviceInfo toDeviceInfo(MonitoredDevice device) {
        return new DeviceInfo(
            device.getDeviceId(),
            device.getDeviceName() != null ? device.getDeviceName() : "Unknown Device",
            device.getIpAddress() != null ? device.getIpAddress() : "Unknown IP"
        );
    }
}