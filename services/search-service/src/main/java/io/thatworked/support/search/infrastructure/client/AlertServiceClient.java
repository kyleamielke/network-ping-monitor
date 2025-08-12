package io.thatworked.support.search.infrastructure.client;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * Feign client for communicating with the alert service.
 */
@FeignClient(name = "alert-service", fallback = AlertServiceClientFallback.class)
public interface AlertServiceClient {
    // Alert search endpoints to be implemented when needed
}