package io.thatworked.support.device.domain.model;

/**
 * Enumeration of device types in the system.
 */
public enum DeviceType {
    SERVER("Server"),
    ROUTER("Router"),
    SWITCH("Switch"),
    WORKSTATION("Workstation"),
    LAPTOP("Laptop"),
    PRINTER("Printer"),
    FIREWALL("Firewall"),
    LOAD_BALANCER("Load Balancer"),
    STORAGE("Storage"),
    VIRTUAL_MACHINE("Virtual Machine"),
    CONTAINER("Container"),
    IOT_DEVICE("IoT Device"),
    MOBILE_DEVICE("Mobile Device"),
    OTHER("Other");

    private final String displayName;

    DeviceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get DeviceType from string value, returns null if not a valid type
     */
    public static DeviceType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        // Try exact match first
        try {
            return DeviceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // Try display name match
            for (DeviceType type : values()) {
                if (type.displayName.equalsIgnoreCase(value)) {
                    return type;
                }
            }
            // Return null instead of OTHER for unknown values
            return null;
        }
    }
}