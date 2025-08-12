package io.thatworked.support.report.infrastructure.client;

import io.thatworked.support.report.infrastructure.dto.PingResultDTO;
import io.thatworked.support.report.infrastructure.dto.PingStatisticsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ping-service", fallback = PingServiceClientFallback.class)
public interface PingServiceClient {
    
    @GetMapping("/api/v1/ping/report-statistics/{deviceId}")
    PingStatisticsDTO getPingStatistics(
        @PathVariable("deviceId") String deviceId,
        @RequestParam("startTime") String startTime,
        @RequestParam("endTime") String endTime
    );
    
    @GetMapping("/api/v1/ping/report-statistics/all")
    List<PingStatisticsDTO> getAllDeviceStatistics(
        @RequestParam("startTime") String startTime,
        @RequestParam("endTime") String endTime
    );
}