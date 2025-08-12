package io.thatworked.support.ping.application.service;

import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.domain.PingTarget;
import io.thatworked.support.ping.domain.MonitoredDevice;
import io.thatworked.support.ping.api.dto.PingReportStatisticsDTO;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingResultRepository;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingTargetRepository;
import io.thatworked.support.ping.infrastructure.repository.jpa.MonitoredDeviceRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PingReportService {
    private final StructuredLogger logger;
    private final PingResultRepository pingResultRepository;
    private final PingTargetRepository pingTargetRepository;
    private final MonitoredDeviceRepository monitoredDeviceRepository;
    
    public PingReportService(StructuredLoggerFactory structuredLoggerFactory,
                           PingResultRepository pingResultRepository,
                           PingTargetRepository pingTargetRepository,
                           MonitoredDeviceRepository monitoredDeviceRepository) {
        this.logger = structuredLoggerFactory.getLogger(PingReportService.class);
        this.pingResultRepository = pingResultRepository;
        this.pingTargetRepository = pingTargetRepository;
        this.monitoredDeviceRepository = monitoredDeviceRepository;
    }

    @Transactional(readOnly = true)
    public List<PingReportStatisticsDTO> getAllDeviceStatistics(Instant startTime, Instant endTime) {
        List<PingTarget> allTargets = pingTargetRepository.findAll();
        List<PingReportStatisticsDTO> statistics = new ArrayList<>();
        
        for (PingTarget target : allTargets) {
            if (target.isMonitored()) {
                PingReportStatisticsDTO stat = getDeviceStatisticsInternal(target.getDeviceId(), startTime, endTime);
                
                // Fetch device name from MonitoredDevice
                monitoredDeviceRepository.findById(target.getDeviceId())
                    .ifPresent(device -> stat.setDeviceName(device.getDeviceName()));
                
                stat.setTargetIp(target.getIpAddress());
                stat.setTargetHostname(target.getHostname());
                statistics.add(stat);
            }
        }
        
        return statistics;
    }
    
    @Transactional(readOnly = true)
    public PingReportStatisticsDTO getDeviceStatistics(UUID deviceId, Instant startTime, Instant endTime) {
        PingTarget target = pingTargetRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Device not found: " + deviceId));
        
        PingReportStatisticsDTO stat = getDeviceStatisticsInternal(deviceId, startTime, endTime);
        
        // Fetch device name from MonitoredDevice
        monitoredDeviceRepository.findById(deviceId)
            .ifPresent(device -> stat.setDeviceName(device.getDeviceName()));
        
        stat.setTargetIp(target.getIpAddress());
        stat.setTargetHostname(target.getHostname());
        
        return stat;
    }
    
    private PingReportStatisticsDTO getDeviceStatisticsInternal(UUID deviceId, Instant startInstant, Instant endInstant) {
        List<PingResult> results = pingResultRepository.findByDeviceIdAndTimeBetween(deviceId, startInstant, endInstant);
        
        PingReportStatisticsDTO stat = new PingReportStatisticsDTO();
        stat.setDeviceId(deviceId.toString());
        stat.setPeriodStart(startInstant);
        stat.setPeriodEnd(endInstant);
        
        if (results.isEmpty()) {
            stat.setTotalPings(0);
            stat.setSuccessfulPings(0);
            stat.setFailedPings(0);
            stat.setSuccessRate(0.0);
            stat.setUptimePercentage(0.0);
            stat.setUptimeSeconds(0);
            stat.setDowntimeSeconds(0);
            return stat;
        }
        
        // Calculate statistics
        long totalPings = results.size();
        long successfulPings = results.stream()
                .filter(r -> r.getStatus().isSuccess())
                .count();
        long failedPings = totalPings - successfulPings;
        
        stat.setTotalPings(totalPings);
        stat.setSuccessfulPings(successfulPings);
        stat.setFailedPings(failedPings);
        stat.setSuccessRate(totalPings > 0 ? (double) successfulPings / totalPings * 100 : 0.0);
        
        // Calculate response time statistics for successful pings
        List<Double> responseTimes = results.stream()
                .filter(r -> r.getStatus().isSuccess() && r.getRoundTripTime() != null)
                .map(PingResult::getRoundTripTime)
                .collect(Collectors.toList());
        
        if (!responseTimes.isEmpty()) {
            stat.setAverageResponseTime(responseTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0));
            stat.setMinResponseTime(responseTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .min()
                    .orElse(0.0));
            stat.setMaxResponseTime(responseTimes.stream()
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(0.0));
        }
        
        // Calculate uptime/downtime
        // Assuming pings are at regular intervals (e.g., every 5 seconds)
        long pingIntervalSeconds = 5; // This should match your actual ping interval
        long uptimeSeconds = successfulPings * pingIntervalSeconds;
        long downtimeSeconds = failedPings * pingIntervalSeconds;
        
        stat.setUptimeSeconds(uptimeSeconds);
        stat.setDowntimeSeconds(downtimeSeconds);
        stat.setUptimePercentage(stat.getSuccessRate()); // Uptime percentage equals success rate
        
        return stat;
    }
}