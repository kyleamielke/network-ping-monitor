package io.thatworked.support.search.api.dto.response;

import java.util.Map;

/**
 * Response DTO for health check operations.
 */
public class HealthResponse {
    
    private String status;
    private String serviceStatus;
    private Map<String, Boolean> providers;
    
    public HealthResponse() {}
    
    public HealthResponse(String status, String serviceStatus, Map<String, Boolean> providers) {
        this.status = status;
        this.serviceStatus = serviceStatus;
        this.providers = providers;
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getServiceStatus() {
        return serviceStatus;
    }
    
    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }
    
    public Map<String, Boolean> getProviders() {
        return providers;
    }
    
    public void setProviders(Map<String, Boolean> providers) {
        this.providers = providers;
    }
}