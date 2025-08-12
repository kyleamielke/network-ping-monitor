package io.thatworked.support.ping.application.service;

import io.thatworked.support.ping.domain.PingResult;
import io.thatworked.support.ping.api.dto.PingStatisticsDTO;
import io.thatworked.support.common.exception.EntityNotFoundException;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingTargetRepository;
import io.thatworked.support.ping.infrastructure.repository.jpa.PingResultRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PingStatisticsService {
    private final StructuredLogger logger;
    private final PingResultRepository pingResultRepository;
    private final PingTargetRepository pingTargetRepository;
    
    public PingStatisticsService(StructuredLoggerFactory structuredLoggerFactory,
                               PingResultRepository pingResultRepository,
                               PingTargetRepository pingTargetRepository) {
        this.logger = structuredLoggerFactory.getLogger(PingStatisticsService.class);
        this.pingResultRepository = pingResultRepository;
        this.pingTargetRepository = pingTargetRepository;
    }

    @Transactional(readOnly = true)
    public List<PingResult> getRecentPingResults(UUID deviceId, int limit) {
        if (!pingTargetRepository.existsById(deviceId)) {
            throw new EntityNotFoundException("PingTarget", deviceId.toString());
        }

        return pingResultRepository.findLatestByDeviceId(deviceId, limit);
    }

    @Transactional(readOnly = true)
    public List<PingResult> getPingResultsSince(UUID deviceId, Instant since) {
        if (!pingTargetRepository.existsById(deviceId)) {
            throw new EntityNotFoundException("PingTarget", deviceId.toString());
        }

        return pingResultRepository.findByDeviceIdSince(deviceId, since);
    }

    @Transactional(readOnly = true)
    public PingStatisticsDTO getStatistics(UUID deviceId, Duration timeframe) {
        if (!pingTargetRepository.existsById(deviceId)) {
            throw new EntityNotFoundException("PingTarget", deviceId.toString());
        }

        Instant since = Instant.now().minus(timeframe);

        Double successRate = pingResultRepository.getSuccessRate(deviceId, since);
        Double averageRtt = pingResultRepository.getAverageRtt(deviceId, since);
        List<PingResult> recentResults = pingResultRepository.findByDeviceIdSince(deviceId, since);

        // Get last result
        PingResult lastResult = recentResults.isEmpty() ? null : recentResults.get(0);

        // Count failures
        long failureCount = recentResults.stream()
                .filter(r -> !r.getStatus().isSuccess())
                .count();

        double successRateValue = successRate != null ? successRate : 0.0;
        long totalSamples = recentResults.size();
        long successfulPings = Math.round(totalSamples * successRateValue);
        
        return PingStatisticsDTO.builder()
                .deviceId(deviceId)
                .successRate(successRateValue)
                .averageRtt(averageRtt != null ? averageRtt : 0.0)
                .recentFailures(failureCount)
                .totalSamples((int) totalSamples)
                .lastStatus(lastResult != null ? lastResult.getStatus().getDisplayName() : "Unknown")
                .lastRtt(lastResult != null ? lastResult.getRoundTripTime() : null)
                .lastTime(lastResult != null ? lastResult.getTime() : null)
                .uptime(successRateValue * 100)  // Convert to percentage
                .packetLoss((1 - successRateValue) * 100)  // Convert to percentage
                .successfulPings(successfulPings)
                .failedPings(failureCount)
                .build();
    }
}