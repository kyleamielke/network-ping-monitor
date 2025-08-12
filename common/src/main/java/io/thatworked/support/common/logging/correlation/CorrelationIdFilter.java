package io.thatworked.support.common.logging.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to ensure correlation ID is present in all HTTP requests
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorrelationIdFilter extends OncePerRequestFilter {
    
    // TODO: Implement CorrelationIdService or replace with direct MDC operations
    // private final CorrelationIdService correlationIdService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String correlationId = extractCorrelationId(request);
        
        try {
            // Add to MDC for logging
            MDC.put(CorrelationIdConstants.CORRELATION_ID_MDC_KEY, correlationId);
            
            // Add to response header
            response.setHeader(CorrelationIdConstants.CORRELATION_ID_HEADER, correlationId);
            
            log.debug("Processing request with correlation ID: {}", correlationId);
            
            filterChain.doFilter(request, response);
            
        } finally {
            // Clean up MDC
            MDC.remove(CorrelationIdConstants.CORRELATION_ID_MDC_KEY);
        }
    }
    
    private String extractCorrelationId(HttpServletRequest request) {
        // Try to get from header first
        String correlationId = request.getHeader(CorrelationIdConstants.CORRELATION_ID_HEADER);
        
        if (!StringUtils.hasText(correlationId)) {
            // Generate new one if not present
            correlationId = java.util.UUID.randomUUID().toString();
            log.debug("Generated new correlation ID: {}", correlationId);
        } else {
            log.debug("Using existing correlation ID: {}", correlationId);
        }
        
        return correlationId;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Don't filter actuator endpoints
        String path = request.getRequestURI();
        return path.startsWith("/actuator/");
    }
}