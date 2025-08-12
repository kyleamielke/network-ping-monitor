package io.thatworked.support.report.domain.model;

/**
 * Enumeration of supported report types with business logic.
 */
public enum ReportType {
    
    UPTIME_SUMMARY("Uptime Summary", true, true),
    DEVICE_STATUS("Device Status", false, false),
    PING_PERFORMANCE("Ping Performance", true, true),
    ALERT_HISTORY("Alert History", true, false);
    
    private final String displayName;
    private final boolean requiresTimeRange;
    private final boolean requiresPingData;
    
    ReportType(String displayName, boolean requiresTimeRange, boolean requiresPingData) {
        this.displayName = displayName;
        this.requiresTimeRange = requiresTimeRange;
        this.requiresPingData = requiresPingData;
    }
    
    /**
     * Returns the human-readable display name for this report type.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Indicates whether this report type requires a time range for generation.
     */
    public boolean requiresTimeRange() {
        return requiresTimeRange;
    }
    
    /**
     * Indicates whether this report type requires ping data to be meaningful.
     */
    public boolean requiresPingData() {
        return requiresPingData;
    }
    
    /**
     * Validates if the report can be generated with the given parameters.
     */
    public boolean canGenerateWith(ReportTimeRange timeRange, boolean hasPingData) {
        if (requiresTimeRange && timeRange == null) {
            return false;
        }
        if (requiresPingData && !hasPingData) {
            return false;
        }
        return true;
    }
}