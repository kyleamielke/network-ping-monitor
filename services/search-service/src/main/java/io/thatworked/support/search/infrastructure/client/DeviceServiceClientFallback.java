package io.thatworked.support.search.infrastructure.client;

import io.thatworked.support.search.infrastructure.dto.DeviceSearchCriteria;
import io.thatworked.support.search.infrastructure.dto.DeviceSearchResult;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DeviceServiceClientFallback implements DeviceServiceClient {
    
    private final StructuredLogger logger;
    
    public DeviceServiceClientFallback(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(DeviceServiceClientFallback.class);
    }
    
    @Override
    public DeviceSearchResult searchDevices(DeviceSearchCriteria criteria) {
        logger.with("operation", "searchDevices")
                .with("fallbackReason", "serviceUnavailable")
                .with("resultCount", 0)
                .warn("Device service is unavailable, returning empty results");
        DeviceSearchResult result = new DeviceSearchResult();
        result.setDevices(new ArrayList<>());
        result.setTotalElements(0);
        result.setTotalPages(0);
        result.setCurrentPage(0);
        result.setSize(0);
        return result;
    }
}