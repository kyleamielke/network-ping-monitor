package io.thatworked.support.search.infrastructure.adapter;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.search.domain.model.SearchItem;
import io.thatworked.support.search.domain.model.SearchQuery;
import io.thatworked.support.search.domain.model.SearchResult;
import io.thatworked.support.search.domain.model.SearchType;
import io.thatworked.support.search.domain.port.SearchProvider;
import io.thatworked.support.search.infrastructure.client.AlertServiceClient;
import io.thatworked.support.search.infrastructure.client.DeviceServiceClient;
import io.thatworked.support.search.infrastructure.client.ReportServiceClient;
import io.thatworked.support.search.infrastructure.mapper.SearchItemMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Search provider adapter that queries individual services directly.
 * Implements the domain SearchProvider port by delegating to infrastructure clients.
 */
@Component
public class ServiceSearchProviderAdapter implements SearchProvider {
    
    private static final long CLIENT_TIMEOUT_MS = 3000;
    
    private final StructuredLogger logger;
    private final DeviceServiceClient deviceServiceClient;
    private final AlertServiceClient alertServiceClient;
    private final ReportServiceClient reportServiceClient;
    private final SearchItemMapper searchItemMapper;
    
    public ServiceSearchProviderAdapter(StructuredLoggerFactory loggerFactory,
                                       DeviceServiceClient deviceServiceClient,
                                       AlertServiceClient alertServiceClient,
                                       ReportServiceClient reportServiceClient,
                                       SearchItemMapper searchItemMapper) {
        this.logger = loggerFactory.getLogger(ServiceSearchProviderAdapter.class);
        this.deviceServiceClient = deviceServiceClient;
        this.alertServiceClient = alertServiceClient;
        this.reportServiceClient = reportServiceClient;
        this.searchItemMapper = searchItemMapper;
    }
    
    @Override
    public SearchResult search(SearchQuery query) {
        long startTime = System.currentTimeMillis();
        
        logger.with("query", query.getQuery())
              .with("type", query.getType().getValue())
              .with("limit", query.getLimit())
              .debug("Executing service search");
        
        List<SearchItem> items = new ArrayList<>();
        
        switch (query.getType()) {
            case DEVICE:
                items = searchDevices(query);
                break;
            case ALERT:
                items = searchAlerts(query);
                break;
            case REPORT:
                items = searchReports(query);
                break;
            case ALL:
                items = searchAll(query);
                break;
        }
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        return SearchResult.create(items, query.getQuery(), searchTime);
    }
    
    @Override
    public String getProviderName() {
        return "ServiceSearchProvider";
    }
    
    @Override
    public boolean isAvailable() {
        // Check if at least one client is available
        return isDeviceServiceAvailable() || isAlertServiceAvailable() || isReportServiceAvailable();
    }
    
    private List<SearchItem> searchDevices(SearchQuery query) {
        if (!isDeviceServiceAvailable()) {
            return new ArrayList<>();
        }
        
        try {
            var searchCriteria = searchItemMapper.toDeviceSearchCriteria(query);
            var searchResult = deviceServiceClient.searchDevices(searchCriteria);
            
            if (searchResult != null && searchResult.getDevices() != null) {
                return searchResult.getDevices().stream()
                    .map(device -> searchItemMapper.fromDeviceDTO(device))
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            logger.with("error", e.getMessage())
                  .with("query", query.getQuery())
                  .error("Device search failed", e);
        }
        
        return new ArrayList<>();
    }
    
    private List<SearchItem> searchAlerts(SearchQuery query) {
        if (!isAlertServiceAvailable()) {
            return new ArrayList<>();
        }
        
        try {
            // Implement alert search when client is available
            logger.with("query", query.getQuery())
                  .debug("Alert search not yet implemented");
        } catch (Exception e) {
            logger.with("error", e.getMessage())
                  .with("query", query.getQuery())
                  .error("Alert search failed", e);
        }
        
        return new ArrayList<>();
    }
    
    private List<SearchItem> searchReports(SearchQuery query) {
        if (!isReportServiceAvailable()) {
            return new ArrayList<>();
        }
        
        try {
            // Implement report search when client is available
            logger.with("query", query.getQuery())
                  .debug("Report search not yet implemented");
        } catch (Exception e) {
            logger.with("error", e.getMessage())
                  .with("query", query.getQuery())
                  .error("Report search failed", e);
        }
        
        return new ArrayList<>();
    }
    
    private List<SearchItem> searchAll(SearchQuery query) {
        // Divide limit among types
        int perTypeLimit = Math.max(query.getLimit() / 3, 3);
        
        List<CompletableFuture<List<SearchItem>>> futures = new ArrayList<>();
        
        // Create futures for parallel execution
        futures.add(CompletableFuture.supplyAsync(() -> 
            searchDevices(query.withType(SearchType.DEVICE))
        ).orTimeout(CLIENT_TIMEOUT_MS, TimeUnit.MILLISECONDS));
        
        futures.add(CompletableFuture.supplyAsync(() -> 
            searchAlerts(query.withType(SearchType.ALERT))
        ).orTimeout(CLIENT_TIMEOUT_MS, TimeUnit.MILLISECONDS));
        
        futures.add(CompletableFuture.supplyAsync(() -> 
            searchReports(query.withType(SearchType.REPORT))
        ).orTimeout(CLIENT_TIMEOUT_MS, TimeUnit.MILLISECONDS));
        
        // Wait for all searches to complete
        List<SearchItem> allItems = new ArrayList<>();
        for (CompletableFuture<List<SearchItem>> future : futures) {
            try {
                allItems.addAll(future.get(CLIENT_TIMEOUT_MS, TimeUnit.MILLISECONDS));
            } catch (Exception e) {
                // Individual search failure already logged
            }
        }
        
        // Sort by relevance and limit
        return allItems.stream()
            .sorted((a, b) -> Double.compare(b.getRelevanceScore(), a.getRelevanceScore()))
            .limit(query.getLimit())
            .collect(Collectors.toList());
    }
    
    private boolean isDeviceServiceAvailable() {
        try {
            // Simple availability check - could be enhanced
            return deviceServiceClient != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isAlertServiceAvailable() {
        try {
            return alertServiceClient != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isReportServiceAvailable() {
        try {
            return reportServiceClient != null;
        } catch (Exception e) {
            return false;
        }
    }
}