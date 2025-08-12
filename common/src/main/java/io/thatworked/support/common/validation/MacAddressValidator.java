package io.thatworked.support.common.validation;

import java.util.regex.Pattern;

/**
 * Validator for MAC (Media Access Control) addresses.
 * Supports multiple common MAC address formats used by different vendors and systems.
 */
public class MacAddressValidator {
    
    // Standard formats
    private static final Pattern COLON_SEPARATED = Pattern.compile(
        "^([0-9A-Fa-f]{2}[:]){5}([0-9A-Fa-f]{2})$"  // ff:ff:ff:ff:ff:ff
    );
    
    private static final Pattern DASH_SEPARATED = Pattern.compile(
        "^([0-9A-Fa-f]{2}[-]){5}([0-9A-Fa-f]{2})$"  // ff-ff-ff-ff-ff-ff
    );
    
    private static final Pattern DOT_SEPARATED = Pattern.compile(
        "^([0-9A-Fa-f]{2}[.]){5}([0-9A-Fa-f]{2})$"  // ff.ff.ff.ff.ff.ff
    );
    
    // Cisco format
    private static final Pattern CISCO_FORMAT = Pattern.compile(
        "^([0-9A-Fa-f]{4}[.]){2}([0-9A-Fa-f]{4})$"  // ffff.ffff.ffff
    );
    
    // Compact formats
    private static final Pattern NO_SEPARATOR = Pattern.compile(
        "^[0-9A-Fa-f]{12}$"  // ffffffffffff
    );
    
    private static final Pattern TWO_GROUPS_DASH = Pattern.compile(
        "^[0-9A-Fa-f]{6}[-][0-9A-Fa-f]{6}$"  // ffffff-ffffff
    );
    
    private static final Pattern THREE_GROUPS_COLON = Pattern.compile(
        "^([0-9A-Fa-f]{4}[:]){2}([0-9A-Fa-f]{4})$"  // ffff:ffff:ffff
    );
    
    /**
     * Validates if the given string is a valid MAC address in any supported format.
     * 
     * Supported formats:
     * - ff:ff:ff:ff:ff:ff (IEEE 802 standard, Unix/Linux)
     * - ff-ff-ff-ff-ff-ff (Windows)
     * - ff.ff.ff.ff.ff.ff (Uncommon but used)
     * - ffff.ffff.ffff (Cisco)
     * - ffffffffffff (No separators)
     * - ffffff-ffffff (Two groups)
     * - ffff:ffff:ffff (Three groups)
     * 
     * @param macAddress the MAC address string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValid(String macAddress) {
        if (macAddress == null || macAddress.trim().isEmpty()) {
            return false;
        }
        
        String mac = macAddress.trim();
        
        return COLON_SEPARATED.matcher(mac).matches() ||
               DASH_SEPARATED.matcher(mac).matches() ||
               DOT_SEPARATED.matcher(mac).matches() ||
               CISCO_FORMAT.matcher(mac).matches() ||
               NO_SEPARATOR.matcher(mac).matches() ||
               TWO_GROUPS_DASH.matcher(mac).matches() ||
               THREE_GROUPS_COLON.matcher(mac).matches();
    }
    
    /**
     * Normalizes a MAC address to the standard colon-separated format (ff:ff:ff:ff:ff:ff).
     * 
     * @param macAddress the MAC address to normalize
     * @return normalized MAC address in uppercase with colon separators, or null if invalid
     */
    public static String normalize(String macAddress) {
        if (!isValid(macAddress)) {
            return null;
        }
        
        // Remove all separators and convert to uppercase
        String cleanMac = macAddress.trim()
            .replaceAll("[:.\\-]", "")
            .toUpperCase();
        
        // Ensure we have exactly 12 hex characters
        if (cleanMac.length() != 12) {
            return null;
        }
        
        // Format as XX:XX:XX:XX:XX:XX
        StringBuilder normalized = new StringBuilder();
        for (int i = 0; i < 12; i += 2) {
            if (i > 0) {
                normalized.append(":");
            }
            normalized.append(cleanMac.substring(i, i + 2));
        }
        
        return normalized.toString();
    }
    
    /**
     * Normalizes a MAC address to a clean lowercase string with no separators.
     * This is the standard format for database storage.
     * Example: "AA:BB:CC:DD:EE:FF" becomes "aabbccddeeff"
     * 
     * @param macAddress the MAC address to normalize
     * @return normalized MAC address as lowercase hex string, or null if invalid
     */
    public static String normalizeForStorage(String macAddress) {
        if (!isValid(macAddress)) {
            return null;
        }
        
        // Remove all separators and convert to lowercase
        String cleanMac = macAddress.trim()
            .replaceAll("[:.\\-]", "")
            .toLowerCase();
        
        // Ensure we have exactly 12 hex characters
        if (cleanMac.length() != 12) {
            return null;
        }
        
        return cleanMac;
    }
    
    /**
     * Formats a MAC address to a specific format.
     * 
     * @param macAddress the MAC address to format
     * @param format the desired format
     * @return formatted MAC address, or null if invalid
     */
    public static String format(String macAddress, MacAddressFormat format) {
        String normalized = normalize(macAddress);
        if (normalized == null) {
            return null;
        }
        
        // Remove colons for reformatting
        String clean = normalized.replaceAll(":", "");
        
        switch (format) {
            case COLON_SEPARATED:
                return normalized; // Already in this format
                
            case DASH_SEPARATED:
                return formatWithSeparator(clean, 2, "-");
                
            case DOT_SEPARATED:
                return formatWithSeparator(clean, 2, ".");
                
            case CISCO:
                return formatWithSeparator(clean, 4, ".");
                
            case NO_SEPARATOR:
                return clean;
                
            case TWO_GROUPS:
                return clean.substring(0, 6) + "-" + clean.substring(6);
                
            case THREE_GROUPS:
                return formatWithSeparator(clean, 4, ":");
                
            default:
                return normalized;
        }
    }
    
    /**
     * Enum representing different MAC address formats.
     */
    public enum MacAddressFormat {
        COLON_SEPARATED,  // ff:ff:ff:ff:ff:ff
        DASH_SEPARATED,   // ff-ff-ff-ff-ff-ff
        DOT_SEPARATED,    // ff.ff.ff.ff.ff.ff
        CISCO,            // ffff.ffff.ffff
        NO_SEPARATOR,     // ffffffffffff
        TWO_GROUPS,       // ffffff-ffffff
        THREE_GROUPS      // ffff:ffff:ffff
    }
    
    private static String formatWithSeparator(String mac, int groupSize, String separator) {
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < mac.length(); i += groupSize) {
            if (i > 0) {
                formatted.append(separator);
            }
            formatted.append(mac.substring(i, Math.min(i + groupSize, mac.length())));
        }
        return formatted.toString();
    }
}