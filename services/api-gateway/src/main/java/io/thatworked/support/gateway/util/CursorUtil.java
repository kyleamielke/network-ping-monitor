package io.thatworked.support.gateway.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility class for cursor encoding and decoding.
 * Cursors are Base64-encoded strings that contain pagination information.
 */
public class CursorUtil {
    
    private CursorUtil() {
        // Utility class
    }
    
    /**
     * Encode a cursor string from an ID and optional timestamp.
     * Format: "type:id:timestamp" encoded in Base64
     */
    public static String encodeCursor(String type, String id, Long timestamp) {
        String cursorData = String.format("%s:%s:%d", type, id, timestamp != null ? timestamp : System.currentTimeMillis());
        return Base64.getEncoder().encodeToString(cursorData.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Encode a simple cursor from just an ID.
     */
    public static String encodeCursor(String type, String id) {
        return encodeCursor(type, id, null);
    }
    
    /**
     * Decode a cursor back to its components.
     * Returns [type, id, timestamp] array
     */
    public static String[] decodeCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return new String[0];
        }
        
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            return decoded.split(":", 3);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid cursor: " + cursor, e);
        }
    }
    
    /**
     * Extract the ID from a cursor.
     */
    public static String extractId(String cursor) {
        String[] parts = decodeCursor(cursor);
        return parts.length >= 2 ? parts[1] : null;
    }
    
    /**
     * Extract the type from a cursor.
     */
    public static String extractType(String cursor) {
        String[] parts = decodeCursor(cursor);
        return parts.length >= 1 ? parts[0] : null;
    }
    
    /**
     * Extract the timestamp from a cursor.
     */
    public static Long extractTimestamp(String cursor) {
        String[] parts = decodeCursor(cursor);
        if (parts.length >= 3) {
            try {
                return Long.parseLong(parts[2]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}