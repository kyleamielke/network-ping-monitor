package io.thatworked.support.device.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Database configuration properties.
 */
@Data
public class DatabaseProperties {
    
    /**
     * Maximum number of connections in the pool
     */
    @Min(value = 1, message = "Pool size must be at least 1")
    private int poolSize = 10;
    
    /**
     * Minimum idle connections in the pool
     */
    @Min(value = 0, message = "Minimum idle cannot be negative")
    private int minimumIdle = 5;
    
    /**
     * Connection timeout
     */
    @NotNull(message = "Connection timeout is required")
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration connectionTimeout = Duration.ofSeconds(30);
    
    /**
     * Idle timeout for connections
     */
    @NotNull(message = "Idle timeout is required")
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration idleTimeout = Duration.ofMinutes(10);
    
    /**
     * Maximum lifetime of a connection
     */
    @NotNull(message = "Max lifetime is required")
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration maxLifetime = Duration.ofMinutes(30);
    
    /**
     * Query timeout
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration queryTimeout = Duration.ofSeconds(10);
    
    /**
     * Enable SQL logging
     */
    private boolean showSql = false;
    
    /**
     * Enable SQL formatting
     */
    private boolean formatSql = false;
    
    /**
     * Batch size for bulk operations
     */
    @Min(value = 1, message = "Batch size must be at least 1")
    private int batchSize = 50;
}