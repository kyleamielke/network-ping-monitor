package io.thatworked.support.search.infrastructure.client;

import io.thatworked.support.search.infrastructure.dto.DeviceSearchCriteria;
import io.thatworked.support.search.infrastructure.dto.DeviceSearchResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "device-service", fallback = DeviceServiceClientFallback.class)
public interface DeviceServiceClient {
    
    @PostMapping("/api/v1/devices/search")
    DeviceSearchResult searchDevices(@RequestBody DeviceSearchCriteria criteria);
}