package io.thatworked.support.gateway.util;

import java.util.HashMap;
import java.util.Map;

public class DeviceTypeMapper {
    
    private static final Map<String, String> ENUM_TO_DISPLAY = new HashMap<>();
    private static final Map<String, String> DISPLAY_TO_ENUM = new HashMap<>();
    
    static {
        // Initialize the mappings based on DeviceTypeValidator accepted values
        addMapping("SERVER", "Server");
        addMapping("ROUTER", "Network Router");
        addMapping("SWITCH", "Network Switch");
        addMapping("WORKSTATION", "Workstation");
        addMapping("VIRTUAL_MACHINE", "Virtual Machine");
        addMapping("FIREWALL", "Security Device");
        
        // Additional mappings for other DeviceType enum values
        addMapping("LAPTOP", "Laptop");
        addMapping("PRINTER", "Printer");
        addMapping("LOAD_BALANCER", "Load Balancer");
        addMapping("STORAGE", "Storage");
        addMapping("CONTAINER", "Container");
        addMapping("IOT_DEVICE", "IoT Device");
        addMapping("MOBILE_DEVICE", "Mobile Device");
        addMapping("OTHER", "Other");
    }
    
    private static void addMapping(String enumName, String displayName) {
        ENUM_TO_DISPLAY.put(enumName, displayName);
        DISPLAY_TO_ENUM.put(displayName, enumName);
    }
    
    /**
     * Converts device type enum name to display name.
     * Example: "SERVER" -> "Server"
     * 
     * @param enumName The enum name (e.g., "SERVER")
     * @return The display name (e.g., "Server"), or the original value if no mapping exists
     */
    public static String toDisplayName(String enumName) {
        if (enumName == null) {
            return null;
        }
        return ENUM_TO_DISPLAY.getOrDefault(enumName, enumName);
    }
    
    /**
     * Converts display name to device type enum name.
     * Example: "Server" -> "SERVER"
     * 
     * @param displayName The display name (e.g., "Server")
     * @return The enum name (e.g., "SERVER"), or the original value if no mapping exists
     */
    public static String toEnumName(String displayName) {
        if (displayName == null) {
            return null;
        }
        return DISPLAY_TO_ENUM.getOrDefault(displayName, displayName);
    }
}