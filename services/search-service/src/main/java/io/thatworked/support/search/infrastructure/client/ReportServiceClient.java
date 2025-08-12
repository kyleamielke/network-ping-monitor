package io.thatworked.support.search.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Feign client for communicating with the report service.
 */
@FeignClient(name = "report-service", fallback = ReportServiceClientFallback.class)
public interface ReportServiceClient {
    // Report search endpoints to be implemented when needed
}