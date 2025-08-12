package io.thatworked.support.device.api.constants;

/**
 * API-related constants for the device service.
 */
public final class ApiConstants {
    
    private ApiConstants() {
        throw new IllegalStateException("Constants class");
    }
    
    // API Paths
    public static final String API_BASE_PATH = "/api/v1/devices";
    
    // Pagination defaults
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;
}