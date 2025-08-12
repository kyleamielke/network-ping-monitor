package io.thatworked.support.device.domain.model;

/**
 * Domain enum representing device status.
 * Pure domain concept with no framework dependencies.
 */
public enum DeviceStatus {
    ACTIVE("Active", "Device is operational and can be monitored"),
    INACTIVE("Inactive", "Device is not operational"),
    MAINTENANCE("Maintenance", "Device is under maintenance"),
    DECOMMISSIONED("Decommissioned", "Device has been decommissioned");

    private final String displayName;
    private final String description;

    DeviceStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static DeviceStatus fromString(String status) {
        if (status == null) {
            return ACTIVE;
        }
        
        try {
            return DeviceStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Handle legacy status values
            switch (status.toLowerCase()) {
                case "online":
                    return ACTIVE;
                case "offline":
                    return INACTIVE;
                default:
                    return ACTIVE;
            }
        }
    }
}