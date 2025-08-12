package io.thatworked.support.search.application.usecase;

import io.thatworked.support.search.domain.port.DomainLogger;
import io.thatworked.support.search.domain.port.SearchProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Use case for checking the health of the search service and its providers.
 */
@Service
public class CheckHealthUseCase {
    
    private final List<SearchProvider> searchProviders;
    private final DomainLogger logger;
    
    public CheckHealthUseCase(List<SearchProvider> searchProviders, DomainLogger logger) {
        this.searchProviders = searchProviders;
        this.logger = logger;
    }
    
    /**
     * Checks the health of all search providers.
     */
    public HealthStatus execute() {
        Map<String, Boolean> providerStatus = new HashMap<>();
        int availableCount = 0;
        
        for (SearchProvider provider : searchProviders) {
            try {
                boolean isAvailable = provider.isAvailable();
                providerStatus.put(provider.getProviderName(), isAvailable);
                
                if (isAvailable) {
                    availableCount++;
                }
            } catch (Exception e) {
                providerStatus.put(provider.getProviderName(), false);
                
                logger.logBusinessWarning("providerHealthCheckFailed", Map.of(
                    "provider", provider.getProviderName(),
                    "error", e.getMessage()
                ));
            }
        }
        
        boolean isHealthy = availableCount > 0;
        String status = isHealthy ? "healthy" : "unhealthy";
        
        logger.logBusinessEvent("healthCheckCompleted", Map.of(
            "status", status,
            "totalProviders", searchProviders.size(),
            "availableProviders", availableCount,
            "providerStatus", providerStatus
        ));
        
        return new HealthStatus(isHealthy, status, providerStatus);
    }
    
    /**
     * Health status result.
     */
    public static class HealthStatus {
        private final boolean healthy;
        private final String status;
        private final Map<String, Boolean> providerStatus;
        
        public HealthStatus(boolean healthy, String status, Map<String, Boolean> providerStatus) {
            this.healthy = healthy;
            this.status = status;
            this.providerStatus = new HashMap<>(providerStatus);
        }
        
        public boolean isHealthy() {
            return healthy;
        }
        
        public String getStatus() {
            return status;
        }
        
        public Map<String, Boolean> getProviderStatus() {
            return new HashMap<>(providerStatus);
        }
    }
}