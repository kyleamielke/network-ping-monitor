package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.infrastructure.dto.DeviceDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "device-service", fallback = DeviceServiceClientFallback.class)
public interface DeviceServiceClient {
    
    @GetMapping("/api/v1/devices/all")
    List<DeviceDTO> getAllDevices();
    
    @GetMapping("/api/v1/devices/{id}")
    DeviceDTO getDevice(@PathVariable("id") String id);
}