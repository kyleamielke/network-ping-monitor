package io.thatworked.support.notification.application.dto;

import java.time.Instant;

/**
 * Query DTO for retrieving notification history.
 * Contains primitive types only for clean architecture boundaries.
 */
public class NotificationHistoryQuery {
    private final Instant startTime;
    private final Instant endTime;
    private final boolean includeFailedOnly;
    
    public NotificationHistoryQuery(Instant startTime, Instant endTime, boolean includeFailedOnly) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.includeFailedOnly = includeFailedOnly;
    }
    
    public Instant getStartTime() {
        return startTime;
    }
    
    public Instant getEndTime() {
        return endTime;
    }
    
    public boolean isIncludeFailedOnly() {
        return includeFailedOnly;
    }
}