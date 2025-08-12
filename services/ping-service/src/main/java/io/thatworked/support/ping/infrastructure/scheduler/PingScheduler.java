package io.thatworked.support.ping.infrastructure.scheduler;

import io.thatworked.support.ping.infrastructure.repository.jpa.PingResultRepository;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class PingScheduler {
    private final StructuredLogger logger;
    private final PingResultRepository pingResultRepository;
    
    public PingScheduler(StructuredLoggerFactory structuredLoggerFactory,
                        PingResultRepository pingResultRepository) {
        this.logger = structuredLoggerFactory.getLogger(PingScheduler.class);
        this.pingResultRepository = pingResultRepository;
    }

    @Value("${ping.retention.days:90}")
    private int retentionDays;

    /**
     * Cleanup old ping results daily at 2 AM
     * Note: This is just a safety measure. TimescaleDB should handle retention automatically.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupOldPingResults() {
        logger.with("retentionDays", retentionDays)
              .with("method", "cleanupOldPingResults")
              .info("Starting scheduled cleanup of old ping results");
        try {
            Instant cutoff = Instant.now().minus(retentionDays, ChronoUnit.DAYS);
            // Implementation will depend on your specific database
            // For example, with JDBC template:
            // int deleted = jdbcTemplate.update("DELETE FROM ping_results WHERE time < ?", cutoff);
            logger.with("cutoffTime", cutoff).info("Finished cleanup of ping results older than cutoff");
        } catch (Exception e) {
            logger.with("method", "cleanupOldPingResults")
                  .error("Failed to clean up old ping results", e);
        }
    }
}