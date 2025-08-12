package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.domain.port.DeviceDataPort;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.report.infrastructure.dto.DeviceDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Infrastructure adapter for accessing device data from the device service.
 */
@Component
public class DeviceDataAdapter implements DeviceDataPort {
    
    private final DeviceServiceClient deviceServiceClient;
    private final StructuredLogger logger;
    
    public DeviceDataAdapter(DeviceServiceClient deviceServiceClient, StructuredLoggerFactory loggerFactory) {
        this.deviceServiceClient = deviceServiceClient;
        this.logger = loggerFactory.getLogger(DeviceDataAdapter.class);
    }
    
    @Override
    public List<DeviceData> getAllDevices() {
        try {
            logger.with("operation", "getAllDevices").info("Fetching all devices from device service");
            
            List<DeviceDTO> devices = deviceServiceClient.getAllDevices();
            
            logger.with("operation", "getAllDevices")
                  .with("deviceCount", devices.size())
                  .info("Successfully fetched devices from device service");
            
            return devices.stream()
                    .map(this::mapToDeviceData)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            logger.with("operation", "getAllDevices")
                  .error("Failed to fetch devices from device service", e);
            throw new RuntimeException("Failed to fetch devices", e);
        }
    }
    
    @Override
    public List<DeviceData> getDevices(List<UUID> deviceIds) {
        try {
            logger.with("operation", "getDevices")
                  .with("deviceIdCount", deviceIds.size())
                  .info("Fetching specific devices from device service");
            
            List<DeviceData> devices = deviceIds.stream()
                    .map(deviceId -> {
                        try {
                            DeviceDTO device = deviceServiceClient.getDevice(deviceId.toString());
                            return device != null ? mapToDeviceData(device) : null;
                        } catch (Exception e) {
                            logger.with("operation", "getDevices")
                                  .with("deviceId", deviceId.toString())
                                  .with("error", e.getMessage())
                                  .warn("Failed to fetch device");
                            return null;
                        }
                    })
                    .filter(device -> device != null)
                    .collect(Collectors.toList());
            
            logger.with("operation", "getDevices")
                  .with("requestedCount", deviceIds.size())
                  .with("retrievedCount", devices.size())
                  .info("Successfully fetched specific devices from device service");
            
            return devices;
            
        } catch (Exception e) {
            logger.with("operation", "getDevices")
                  .with("deviceIdCount", deviceIds.size())
                  .error("Failed to fetch specific devices from device service", e);
            throw new RuntimeException("Failed to fetch specific devices", e);
        }
    }
    
    @Override
    public DeviceData getDevice(UUID deviceId) {
        try {
            logger.with("operation", "getDevice")
                  .with("deviceId", deviceId.toString())
                  .info("Fetching device from device service");
            
            DeviceDTO device = deviceServiceClient.getDevice(deviceId.toString());
            
            if (device == null) {
                logger.with("operation", "getDevice")
                      .with("deviceId", deviceId.toString())
                      .warn("Device not found in device service");
                return null;
            }
            
            logger.with("operation", "getDevice")
                  .with("deviceId", deviceId.toString())
                  .with("deviceName", device.getName())
                  .info("Successfully fetched device from device service");
            
            return mapToDeviceData(device);
            
        } catch (Exception e) {
            logger.with("operation", "getDevice")
                  .with("deviceId", deviceId.toString())
                  .error("Failed to fetch device from device service", e);
            throw new RuntimeException("Failed to fetch device", e);
        }
    }
    
    private DeviceData mapToDeviceData(DeviceDTO dto) {
        return new DeviceData(
            dto.getId(),
            dto.getName(),
            dto.getIpAddress(),
            dto.getHostname(),
            dto.getType(), // Use type instead of deviceType
            dto.isActive(),
            dto.isUp(),
            dto.getLocation()
        );
    }
}