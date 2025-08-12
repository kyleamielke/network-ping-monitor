package io.thatworked.support.common.logging.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Map;

/**
 * Standard log context structure for all microservices
 */
@Data
@Builder
@Jacksonized
public class LogContext {
    private Instant timestamp;
    private String level;
    private String service;
    private String correlationId;
    private String message;
    private Map<String, Object> context;
    private ErrorInfo error;
    private PerformanceInfo performance;
    private SecurityInfo security;
    
    @Data
    @Builder
    @Jacksonized
    public static class ErrorInfo {
        private String code;
        private String type;
        private String message;
        private String stackTrace;
        private String severity;
    }
    
    @Data
    @Builder
    @Jacksonized
    public static class PerformanceInfo {
        private Long duration;
        private String operation;
        private Map<String, Object> metrics;
    }
    
    @Data
    @Builder
    @Jacksonized
    public static class SecurityInfo {
        private String event;
        private String user;
        private String resource;
        private String action;
        private Boolean success;
    }
}