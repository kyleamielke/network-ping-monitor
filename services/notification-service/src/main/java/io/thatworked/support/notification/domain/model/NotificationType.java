package io.thatworked.support.notification.domain.model;

/**
 * Types of notifications that can be sent.
 * Pure domain enum with no framework dependencies.
 */
public enum NotificationType {
    DEVICE_DOWN("Device Down Alert", "A device has stopped responding"),
    DEVICE_RECOVERED("Device Recovery", "A device has recovered and is responding"),
    HIGH_LATENCY("High Latency Alert", "Device response time exceeds threshold"),
    PACKET_LOSS("Packet Loss Alert", "Device experiencing packet loss"),
    TEST("Test Notification", "Test notification for validation");
    
    private final String title;
    private final String defaultDescription;
    
    NotificationType(String title, String defaultDescription) {
        this.title = title;
        this.defaultDescription = defaultDescription;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDefaultDescription() {
        return defaultDescription;
    }
}