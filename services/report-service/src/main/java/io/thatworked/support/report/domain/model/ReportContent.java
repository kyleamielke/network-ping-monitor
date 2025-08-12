package io.thatworked.support.report.domain.model;

/**
 * Value object representing the generated content of a report.
 */
public class ReportContent {
    
    private final byte[] data;
    private final long size;
    private final String checksum;
    
    private ReportContent(byte[] data, String checksum) {
        this.data = validateData(data);
        this.size = data.length;
        this.checksum = checksum;
    }
    
    /**
     * Creates report content from byte array.
     */
    public static ReportContent of(byte[] data) {
        String checksum = generateChecksum(data);
        return new ReportContent(data, checksum);
    }
    
    /**
     * Creates empty report content.
     */
    public static ReportContent empty() {
        return new ReportContent(new byte[0], "");
    }
    
    /**
     * Checks if this content is empty.
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Validates the integrity of the content using checksum.
     */
    public boolean isValid() {
        return checksum.equals(generateChecksum(data));
    }
    
    private byte[] validateData(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Report content data cannot be null");
        }
        return data.clone(); // Defensive copy
    }
    
    private static String generateChecksum(byte[] data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return ""; // Fallback to empty checksum
        }
    }
    
    public byte[] getData() {
        return data.clone(); // Defensive copy
    }
    
    public byte[] getBytes() {
        return getData(); // Alias for getData
    }
    
    public long getSize() {
        return size;
    }
    
    public String getChecksum() {
        return checksum;
    }
}