package io.thatworked.support.search.domain.model;

/**
 * Enumeration of search types supported by the system.
 */
public enum SearchType {
    
    /**
     * Search across all entity types.
     */
    ALL("all"),
    
    /**
     * Search only devices.
     */
    DEVICE("device"),
    
    /**
     * Search only alerts.
     */
    ALERT("alert"),
    
    /**
     * Search only reports.
     */
    REPORT("report");
    
    private final String value;
    
    SearchType(String value) {
        this.value = value;
    }
    
    /**
     * Gets the string value of the search type.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Parses a string value to a SearchType.
     * Returns ALL if the value is not recognized.
     */
    public static SearchType fromValue(String value) {
        if (value == null) {
            return ALL;
        }
        
        for (SearchType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        
        return ALL;
    }
    
    @Override
    public String toString() {
        return value;
    }
}