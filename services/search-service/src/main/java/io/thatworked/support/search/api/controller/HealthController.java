package io.thatworked.support.search.api.controller;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.search.api.dto.response.HealthResponse;
import io.thatworked.support.search.application.usecase.CheckHealthUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for health check operations.
 */
@RestController
@RequestMapping("${search-service.service.api.health-base-path:/api/v1/health}")
@CrossOrigin
public class HealthController {
    
    private final StructuredLogger logger;
    private final CheckHealthUseCase checkHealthUseCase;
    
    public HealthController(StructuredLoggerFactory loggerFactory,
                           CheckHealthUseCase checkHealthUseCase) {
        this.logger = loggerFactory.getLogger(HealthController.class);
        this.checkHealthUseCase = checkHealthUseCase;
    }
    
    /**
     * Checks the health of the search service and its providers.
     */
    @GetMapping
    public ResponseEntity<HealthResponse> checkHealth() {
        logger.with("endpoint", "checkHealth")
              .debug("Health check request received");
        
        try {
            CheckHealthUseCase.HealthStatus status = checkHealthUseCase.execute();
            
            HealthResponse response = new HealthResponse(
                status.getStatus(),
                status.isHealthy() ? "UP" : "DOWN",
                status.getProviderStatus()
            );
            
            logger.with("status", response.getStatus())
                  .with("serviceStatus", response.getServiceStatus())
                  .debug("Health check completed");
            
            return status.isHealthy() 
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(503).body(response);
        } catch (Exception e) {
            logger.with("error", e.getMessage())
                  .error("Health check failed", e);
            
            return ResponseEntity.status(503).body(
                new HealthResponse("error", "DOWN", null)
            );
        }
    }
}