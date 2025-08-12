package io.thatworked.support.notification.domain.model;

/**
 * Available channels for sending notifications.
 * Pure domain enum with no framework dependencies.
 */
public enum NotificationChannel {
    EMAIL("Email", true),
    SLACK("Slack", false),
    TEAMS("Microsoft Teams", false),
    SMS("SMS", false),
    WEBHOOK("Webhook", false);
    
    private final String displayName;
    private final boolean enabled;
    
    NotificationChannel(String displayName, boolean enabled) {
        this.displayName = displayName;
        this.enabled = enabled;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
}