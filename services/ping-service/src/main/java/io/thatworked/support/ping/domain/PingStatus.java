package io.thatworked.support.ping.domain;

/**
 * Enumeration of possible ping result statuses.
 */
public enum PingStatus {
    SUCCESS("Success"),
    FAILURE("Failure"), 
    TIMEOUT("Timeout"),
    ERROR("Error"),
    CIRCUIT_OPEN("CircuitOpen"),
    SKIPPED("Skipped");

    private final String displayName;

    PingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Converts a string to PingStatus enum.
     */
    public static PingStatus fromString(String status) {
        if (status == null) {
            return ERROR;
        }
        
        return switch (status.toUpperCase()) {
            case "SUCCESS" -> SUCCESS;
            case "FAILURE", "FAILED" -> FAILURE;
            case "TIMEOUT" -> TIMEOUT;
            case "ERROR" -> ERROR;
            case "CIRCUITOPEN" -> CIRCUIT_OPEN;
            case "SKIPPED" -> SKIPPED;
            default -> ERROR;
        };
    }

    /**
     * Check if this status represents a successful ping.
     */
    public boolean isSuccess() {
        return this == SUCCESS;
    }

    /**
     * Check if this status represents a failure.
     */
    public boolean isFailure() {
        return this == FAILURE || this == TIMEOUT || this == ERROR;
    }
}