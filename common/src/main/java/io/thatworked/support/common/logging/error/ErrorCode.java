package io.thatworked.support.common.logging.error;

import io.thatworked.support.common.logging.model.LogContext;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Standardized error codes across all services
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Network errors (NET_xxx)
    NET_001("NET_001", "Network connection failed", "ERROR"),
    NET_002("NET_002", "Request timeout", "ERROR"),
    NET_003("NET_003", "DNS resolution failed", "ERROR"),
    
    // Database errors (DB_xxx)
    DB_001("DB_001", "Database connection failed", "CRITICAL"),
    DB_002("DB_002", "Query execution failed", "ERROR"),
    DB_003("DB_003", "Transaction rollback", "ERROR"),
    DB_004("DB_004", "Duplicate key violation", "WARN"),
    
    // Validation errors (VAL_xxx)
    VAL_001("VAL_001", "Invalid input data", "WARN"),
    VAL_002("VAL_002", "Missing required field", "WARN"),
    VAL_003("VAL_003", "Data type mismatch", "WARN"),
    
    // Service errors (SVC_xxx)
    SVC_001("SVC_001", "Service unavailable", "ERROR"),
    SVC_002("SVC_002", "Circuit breaker open", "ERROR"),
    SVC_003("SVC_003", "Rate limit exceeded", "WARN"),
    
    // Kafka errors (KFK_xxx)
    KFK_001("KFK_001", "Message publishing failed", "ERROR"),
    KFK_002("KFK_002", "Message consumption failed", "ERROR"),
    KFK_003("KFK_003", "Deserialization error", "ERROR"),
    
    // Security errors (SEC_xxx)
    SEC_001("SEC_001", "Authentication failed", "WARN"),
    SEC_002("SEC_002", "Authorization denied", "WARN"),
    SEC_003("SEC_003", "Invalid token", "WARN"),
    
    // Business logic errors (BIZ_xxx)
    BIZ_001("BIZ_001", "Business rule violation", "WARN"),
    BIZ_002("BIZ_002", "Invalid state transition", "WARN"),
    BIZ_003("BIZ_003", "Resource not found", "WARN"),
    
    // System errors (SYS_xxx)
    SYS_001("SYS_001", "Out of memory", "CRITICAL"),
    SYS_002("SYS_002", "Disk space low", "ERROR"),
    SYS_003("SYS_003", "Configuration error", "ERROR");
    
    private final String code;
    private final String description;
    private final String severity;
    
    public LogContext.ErrorInfo toErrorInfo(String message) {
        return LogContext.ErrorInfo.builder()
            .code(this.code)
            .type(this.description)
            .message(message)
            .severity(this.severity)
            .build();
    }
}