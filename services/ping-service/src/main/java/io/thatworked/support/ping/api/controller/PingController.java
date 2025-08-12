package io.thatworked.support.ping.api.controller;

import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.api.dto.PingResultDTO;
import io.thatworked.support.ping.api.dto.PingStatisticsDTO;
import io.thatworked.support.ping.api.dto.PingReportStatisticsDTO;
import io.thatworked.support.ping.application.service.PingStatisticsService;
import io.thatworked.support.ping.application.service.PingReportService;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ping")
public class PingController {
    private final StructuredLogger logger;
    private final PingStatisticsService pingStatisticsService;
    private final PingReportService pingReportService;
    
    public PingController(StructuredLoggerFactory structuredLoggerFactory,
                        PingStatisticsService pingStatisticsService,
                        PingReportService pingReportService) {
        this.logger = structuredLoggerFactory.getLogger(PingController.class);
        this.pingStatisticsService = pingStatisticsService;
        this.pingReportService = pingReportService;
    }

    @GetMapping("/results/{deviceId}")
    public ResponseEntity<List<PingResultDTO>> getPingResults(
            @PathVariable UUID deviceId,
            @RequestParam(defaultValue = "50") int limit) {
        List<PingResult> results = pingStatisticsService.getRecentPingResults(deviceId, limit);
        List<PingResultDTO> dtos = results.stream()
                .map(PingResultDTO::fromDomain)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/results/{deviceId}/since")
    public ResponseEntity<List<PingResultDTO>> getPingResultsSince(
            @PathVariable UUID deviceId,
            @RequestParam int minutes) {
        Duration duration = Duration.ofMinutes(minutes);
        Instant since = Instant.now().minus(duration);
        List<PingResult> results = pingStatisticsService.getPingResultsSince(deviceId, since);
        List<PingResultDTO> dtos = results.stream()
                .map(PingResultDTO::fromDomain)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/statistics/{deviceId}")
    public ResponseEntity<PingStatisticsDTO> getPingStatistics(
            @PathVariable UUID deviceId,
            @RequestParam(defaultValue = "60") int minutes) {
        PingStatisticsDTO statistics = pingStatisticsService.getStatistics(
                deviceId, Duration.ofMinutes(minutes));
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/report-statistics/all")
    public ResponseEntity<List<PingReportStatisticsDTO>> getAllDeviceReportStatistics(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        List<PingReportStatisticsDTO> statistics = pingReportService.getAllDeviceStatistics(startTime, endTime);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/report-statistics/{deviceId}")
    public ResponseEntity<PingReportStatisticsDTO> getDeviceReportStatistics(
            @PathVariable UUID deviceId,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        PingReportStatisticsDTO statistics = pingReportService.getDeviceStatistics(deviceId, startTime, endTime);
        return ResponseEntity.ok(statistics);
    }
}