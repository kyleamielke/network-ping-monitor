package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.infrastructure.dto.PingResultDTO;
import io.thatworked.support.report.infrastructure.dto.PingStatisticsDTO;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class PingServiceClientFallback implements PingServiceClient {
    
    private final StructuredLogger logger;
    
    public PingServiceClientFallback(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(PingServiceClientFallback.class);
    }
    
    @Override
    public PingStatisticsDTO getPingStatistics(String deviceId, String startTime, String endTime) {
        logger.with("operation", "getPingStatistics")
                .with("deviceId", deviceId)
                .with("startTime", startTime)
                .with("endTime", endTime)
                .with("fallback", true)
                .warn("Ping service unavailable, cannot retrieve statistics");
        return null;
    }
    
    @Override
    public List<PingStatisticsDTO> getAllDeviceStatistics(String startTime, String endTime) {
        logger.with("operation", "getAllDeviceStatistics")
                .with("startTime", startTime)
                .with("endTime", endTime)
                .with("fallback", true)
                .warn("Ping service unavailable, returning empty statistics list");
        return Collections.emptyList();
    }
}