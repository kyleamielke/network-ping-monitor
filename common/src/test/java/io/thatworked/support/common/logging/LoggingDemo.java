package io.thatworked.support.common.logging;

import io.thatworked.support.common.logging.correlation.CorrelationIdConstants;
import io.thatworked.support.common.logging.error.ErrorCode;
import org.slf4j.MDC;

import java.util.Map;
import java.util.UUID;

/**
 * Demo of how to use the structured logging library
 */
public class LoggingDemo {
    
    public static void main(String[] args) throws Exception {
        // Create a logger
        StructuredLogger log = StructuredLogger.getLogger(LoggingDemo.class, "demo-service");
        
        // Set correlation ID (normally done by CorrelationIdFilter)
        String correlationId = UUID.randomUUID().toString();
        MDC.put(CorrelationIdConstants.CORRELATION_ID_MDC_KEY, correlationId);
        
        System.out.println("=== Structured Logging Demo ===\n");
        
        // 1. Simple logging
        System.out.println("1. Simple info log:");
        log.info("Application started");
        
        // 2. Logging with context
        System.out.println("\n2. Log with context:");
        log.with("userId", "user123")
           .with("action", "login")
           .with("ipAddress", "192.168.1.100")
           .info("User authentication successful");
        
        // 3. Error logging with error code
        System.out.println("\n3. Error log with error code:");
        try {
            throw new RuntimeException("Database connection failed");
        } catch (Exception e) {
            log.with("database", "primary")
               .with("errorCode", ErrorCode.DB_001.getCode())
               .error("Failed to connect to database", e);
        }
        
        // 4. Performance logging
        System.out.println("\n4. Performance log:");
        String result = log.measure("database.query", () -> {
            Thread.sleep(150); // Simulate database query
            return "query result";
        });
        
        // 5. Security event logging
        System.out.println("\n5. Security event log:");
        log.security("LOGIN_ATTEMPT", "user123", "/api/login", "POST", true);
        
        // 6. Warning log
        System.out.println("\n6. Warning log:");
        log.with("cacheSize", 95)
           .with("maxSize", 100)
           .warn("Cache is nearly full");
        
        // Clean up
        MDC.clear();
        
        System.out.println("\n=== Demo Complete ===");
        System.out.println("Note: In production, logs would be in JSON format");
        System.out.println("Check the console output to see the structured logs");
    }
}