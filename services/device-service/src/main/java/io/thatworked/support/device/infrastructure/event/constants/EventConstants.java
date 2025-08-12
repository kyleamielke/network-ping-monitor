package io.thatworked.support.device.infrastructure.event.constants;

/**
 * Event-related constants for the device service.
 */
public final class EventConstants {
    
    private EventConstants() {
        throw new IllegalStateException("Constants class");
    }
    
    // Kafka Topics
    public static final String DEVICE_EVENTS_TOPIC = "device-events";
    
    // Event Types
    public static final String DEVICE_CREATED_EVENT = "DEVICE_CREATED";
    public static final String DEVICE_UPDATED_EVENT = "DEVICE_UPDATED";
    public static final String DEVICE_DELETED_EVENT = "DEVICE_DELETED";
    public static final String DEVICE_ACTIVATED_EVENT = "DEVICE_ACTIVATED";
    public static final String DEVICE_DEACTIVATED_EVENT = "DEVICE_DEACTIVATED";
    public static final String DEVICE_ASSIGNED_EVENT = "DEVICE_ASSIGNED";
    public static final String DEVICE_UNASSIGNED_EVENT = "DEVICE_UNASSIGNED";
    public static final String DEVICE_ROLES_UPDATED_EVENT = "DEVICE_ROLES_UPDATED";
    public static final String DEVICE_ROLES_CLEARED_EVENT = "DEVICE_ROLES_CLEARED";
}