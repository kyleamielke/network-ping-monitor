package io.thatworked.support.common.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.thatworked.support.common.logging.config.LoggingConfiguration;
import io.thatworked.support.common.logging.correlation.CorrelationIdConstants;
import io.thatworked.support.common.logging.model.LogContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Structured logger wrapper that ensures consistent log format across all services
 */
@RequiredArgsConstructor
public class StructuredLogger {
    
    private final Logger logger;
    private final String serviceName;
    private final ObjectMapper objectMapper;
    
    public static StructuredLogger getLogger(Class<?> clazz, String serviceName) {
        return new StructuredLogger(
            LoggerFactory.getLogger(clazz),
            serviceName,
            LoggingConfiguration.getObjectMapper()
        );
    }
    
    // Info logging
    public void info(String message) {
        info(message, null);
    }
    
    public void info(String message, Map<String, Object> context) {
        log("INFO", message, context, null, null, null);
    }
    
    // Error logging
    public void error(String message, Throwable throwable) {
        error(message, null, throwable);
    }
    
    public void error(String message, Map<String, Object> context, Throwable throwable) {
        LogContext.ErrorInfo errorInfo = null;
        if (throwable != null) {
            errorInfo = LogContext.ErrorInfo.builder()
                .type(throwable.getClass().getSimpleName())
                .message(throwable.getMessage())
                .stackTrace(getStackTraceHead(throwable))
                .severity("ERROR")
                .build();
        }
        log("ERROR", message, context, errorInfo, null, null);
    }
    
    // Warning logging
    public void warn(String message) {
        warn(message, null);
    }
    
    public void warn(String message, Map<String, Object> context) {
        log("WARN", message, context, null, null, null);
    }
    
    // Debug logging
    public void debug(String message) {
        debug(message, null);
    }
    
    public void debug(String message, Map<String, Object> context) {
        if (logger.isDebugEnabled()) {
            log("DEBUG", message, context, null, null, null);
        }
    }
    
    // Performance logging
    public void performance(String operation, long duration, Map<String, Object> metrics) {
        LogContext.PerformanceInfo perfInfo = LogContext.PerformanceInfo.builder()
            .operation(operation)
            .duration(duration)
            .metrics(metrics)
            .build();
        log("INFO", "Performance metric", null, null, perfInfo, null);
    }
    
    // Security logging
    public void security(String event, String user, String resource, String action, boolean success) {
        LogContext.SecurityInfo securityInfo = LogContext.SecurityInfo.builder()
            .event(event)
            .user(user)
            .resource(resource)
            .action(action)
            .success(success)
            .build();
        log("INFO", "Security event", null, null, null, securityInfo);
    }
    
    // Measure and log operation duration
    public <T> T measure(String operation, Callable<T> callable) throws Exception {
        long startTime = System.currentTimeMillis();
        try {
            T result = callable.call();
            long duration = System.currentTimeMillis() - startTime;
            performance(operation, duration, Map.of("status", "success"));
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            performance(operation, duration, Map.of("status", "failed", "error", e.getMessage()));
            throw e;
        }
    }
    
    // Context builder for fluent API
    public ContextBuilder with(String key, Object value) {
        return new ContextBuilder().with(key, value);
    }
    
    public class ContextBuilder {
        private final Map<String, Object> context = new HashMap<>();
        
        public ContextBuilder with(String key, Object value) {
            context.put(key, value);
            return this;
        }
        
        public void info(String message) {
            StructuredLogger.this.info(message, context);
        }
        
        public void warn(String message) {
            StructuredLogger.this.warn(message, context);
        }
        
        public void error(String message, Throwable throwable) {
            StructuredLogger.this.error(message, context, throwable);
        }
        
        public void debug(String message) {
            StructuredLogger.this.debug(message, context);
        }
    }
    
    private void log(String level, String message, Map<String, Object> context,
                     LogContext.ErrorInfo error, LogContext.PerformanceInfo performance,
                     LogContext.SecurityInfo security) {
        
        LogContext logContext = LogContext.builder()
            .timestamp(Instant.now())
            .level(level)
            .service(serviceName)
            .correlationId(MDC.get(CorrelationIdConstants.CORRELATION_ID_MDC_KEY))
            .message(message)
            .context(context)
            .error(error)
            .performance(performance)
            .security(security)
            .build();
        
        try {
            String jsonLog = objectMapper.writeValueAsString(logContext);
            
            switch (level) {
                case "ERROR" -> logger.error(jsonLog);
                case "WARN" -> logger.warn(jsonLog);
                case "INFO" -> logger.info(jsonLog);
                case "DEBUG" -> logger.debug(jsonLog);
                default -> logger.info(jsonLog);
            }
        } catch (Exception e) {
            // Fallback to simple logging if JSON serialization fails
            logger.error("Failed to serialize log context: {}", e.getMessage());
            logger.info("{} - {} - {}", level, message, context);
        }
    }
    
    private String getStackTraceHead(Throwable throwable) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace.length > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage());
            for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
                sb.append("\n\tat ").append(stackTrace[i]);
            }
            if (stackTrace.length > 5) {
                sb.append("\n\t... ").append(stackTrace.length - 5).append(" more");
            }
            return sb.toString();
        }
        return throwable.toString();
    }
}