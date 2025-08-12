package io.thatworked.support.report.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Value object representing a time range for report generation.
 */
public class ReportTimeRange {
    
    private final Instant startDate;
    private final Instant endDate;
    
    private ReportTimeRange(Instant startDate, Instant endDate) {
        this.startDate = validateStartDate(startDate);
        this.endDate = validateEndDate(endDate, startDate);
    }
    
    /**
     * Creates a time range from start and end dates.
     */
    public static ReportTimeRange of(Instant startDate, Instant endDate) {
        return new ReportTimeRange(startDate, endDate);
    }
    
    /**
     * Creates a time range for the last specified number of hours.
     */
    public static ReportTimeRange lastHours(int hours) {
        Instant end = Instant.now();
        Instant start = end.minus(hours, ChronoUnit.HOURS);
        return new ReportTimeRange(start, end);
    }
    
    /**
     * Creates a time range for the last specified number of days.
     */
    public static ReportTimeRange lastDays(int days) {
        Instant end = Instant.now();
        Instant start = end.minus(days, ChronoUnit.DAYS);
        return new ReportTimeRange(start, end);
    }
    
    /**
     * Creates a time range for the current day.
     */
    public static ReportTimeRange today() {
        Instant now = Instant.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        return new ReportTimeRange(startOfDay, now);
    }
    
    /**
     * Returns the duration of this time range.
     */
    public Duration getDuration() {
        return Duration.between(startDate, endDate);
    }
    
    /**
     * Checks if this time range is valid (start is before end, not too far in the past/future).
     */
    public boolean isValid() {
        return startDate.isBefore(endDate) &&
               startDate.isAfter(Instant.now().minus(365, ChronoUnit.DAYS)) &&
               endDate.isBefore(Instant.now().plus(1, ChronoUnit.DAYS));
    }
    
    /**
     * Checks if this time range contains the specified date/time.
     */
    public boolean contains(Instant dateTime) {
        return !dateTime.isBefore(startDate) && !dateTime.isAfter(endDate);
    }
    
    private Instant validateStartDate(Instant startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null");
        }
        return startDate;
    }
    
    private Instant validateEndDate(Instant endDate, Instant startDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
        return endDate;
    }
    
    public Instant getStartDate() { return startDate; }
    public Instant getEndDate() { return endDate; }
    
    @Override
    public String toString() {
        return String.format("ReportTimeRange{start=%s, end=%s}", startDate, endDate);
    }
}