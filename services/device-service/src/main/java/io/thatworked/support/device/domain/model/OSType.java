package io.thatworked.support.device.domain.model;

/**
 * Enumeration of operating system types.
 */
public enum OSType {
    WINDOWS("Windows"),
    LINUX("Linux"),
    MACOS("macOS"),
    UNIX("Unix"),
    BSD("BSD"),
    IOS("iOS"),
    ANDROID("Android"),
    EMBEDDED("Embedded"),
    OTHER("Other");

    private final String displayName;

    OSType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get OSType from string value, returns null if not a valid type
     */
    public static OSType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        String normalized = value.toUpperCase().replace(" ", "_");
        
        // Try exact match first
        try {
            return OSType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            // Try common variations
            if (normalized.contains("WIN")) {
                return WINDOWS;
            } else if (normalized.contains("LINUX") || normalized.contains("UBUNTU") || 
                      normalized.contains("DEBIAN") || normalized.contains("CENTOS") ||
                      normalized.contains("RHEL") || normalized.contains("FEDORA")) {
                return LINUX;
            } else if (normalized.contains("MAC") || normalized.contains("DARWIN")) {
                return MACOS;
            } else if (normalized.contains("BSD") || normalized.contains("FREEBSD") || 
                      normalized.contains("OPENBSD")) {
                return BSD;
            } else if (normalized.contains("IOS") || normalized.contains("IPHONE") || 
                      normalized.contains("IPAD")) {
                return IOS;
            } else if (normalized.contains("ANDROID")) {
                return ANDROID;
            }
            
            // Return null instead of OTHER for unknown values
            return null;
        }
    }
}