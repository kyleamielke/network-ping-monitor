package io.thatworked.support.device.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * Validation configuration properties for business rules.
 */
@Data
public class ValidationProperties {
    
    /**
     * Device name validation
     */
    @NotNull(message = "Name validation is required")
    private NameValidation name = new NameValidation();
    
    /**
     * IP address validation
     */
    @NotNull(message = "IP validation is required")
    private IpValidation ip = new IpValidation();
    
    /**
     * Device type validation
     */
    @NotNull(message = "Device type validation is required")
    private DeviceTypeValidation deviceType = new DeviceTypeValidation();
    
    /**
     * Field length limits
     */
    @NotNull(message = "Field limits are required")
    private FieldLimits fieldLimits = new FieldLimits();
    
    @Data
    public static class NameValidation {
        /**
         * Minimum length for device name
         */
        @Min(value = 1, message = "Minimum name length must be at least 1")
        private int minLength = 3;
        
        /**
         * Maximum length for device name
         */
        @Min(value = 1, message = "Maximum name length must be at least 1")
        private int maxLength = 100;
        
        /**
         * Pattern for device name validation
         */
        private String pattern = "^[a-zA-Z0-9][a-zA-Z0-9\\s\\-._]*$";
        
        /**
         * Reserved names that cannot be used
         */
        private Set<String> reservedNames = Set.of("localhost", "unknown", "none");
    }
    
    @Data
    public static class IpValidation {
        /**
         * Allow IPv4 addresses
         */
        private boolean allowIpv4 = true;
        
        /**
         * Allow IPv6 addresses
         */
        private boolean allowIpv6 = true;
        
        /**
         * Allow private IP addresses
         */
        private boolean allowPrivate = true;
        
        /**
         * Blocked IP ranges
         */
        private List<String> blockedRanges = List.of();
        
        /**
         * Reserved IPs that cannot be used
         */
        private Set<String> reservedIps = Set.of("0.0.0.0", "255.255.255.255");
    }
    
    @Data
    public static class DeviceTypeValidation {
        /**
         * Allowed device types
         */
        @NotEmpty(message = "At least one device type must be allowed")
        private Set<String> allowedTypes = Set.of(
            "Server",
            "Network Router",
            "Network Switch",
            "Workstation",
            "Virtual Machine",
            "Security Device"
        );
        
        /**
         * Default device type
         */
        private String defaultType = "Server";
        
        /**
         * Case sensitive validation
         */
        private boolean caseSensitive = true;
    }
    
    @Data
    public static class FieldLimits {
        /**
         * Description max length
         */
        @Min(value = 1, message = "Description max length must be at least 1")
        private int descriptionMaxLength = 500;
        
        /**
         * Location max length
         */
        @Min(value = 1, message = "Location max length must be at least 1")
        private int locationMaxLength = 200;
        
        /**
         * OS name max length
         */
        @Min(value = 1, message = "OS max length must be at least 1")
        private int osMaxLength = 100;
        
        /**
         * OS type max length
         */
        @Min(value = 1, message = "OS type max length must be at least 1")
        private int osTypeMaxLength = 50;
        
        /**
         * Make max length
         */
        @Min(value = 1, message = "Make max length must be at least 1")
        private int makeMaxLength = 100;
        
        /**
         * Model max length
         */
        @Min(value = 1, message = "Model max length must be at least 1")
        private int modelMaxLength = 100;
        
        /**
         * Endpoint ID max length
         */
        @Min(value = 1, message = "Endpoint ID max length must be at least 1")
        private int endpointIdMaxLength = 100;
        
        /**
         * Asset tag max length
         */
        @Min(value = 1, message = "Asset tag max length must be at least 1")
        private int assetTagMaxLength = 100;
    }
}