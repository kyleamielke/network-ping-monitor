package io.thatworked.support.gateway.client;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ReportServiceFallbackFactory implements FallbackFactory<ReportServiceClient> {
    
    private final StructuredLogger logger;
    
    public ReportServiceFallbackFactory(StructuredLoggerFactory loggerFactory) {
        this.logger = loggerFactory.getLogger(ReportServiceFallbackFactory.class);
    }
    
    @Override
    public ReportServiceClient create(Throwable cause) {
        return new ReportServiceClient() {
            
            @Override
            public Map<String, Object> generateReport(Map<String, Object> request) {
                logger.with("operation", "generateReport")
                      .with("reportType", request.get("reportType"))
                      .error("Report service unavailable", cause);
                
                Map<String, Object> fallback = new HashMap<>();
                fallback.put("error", "Report service is currently unavailable");
                fallback.put("message", cause.getMessage());
                return fallback;
            }
            
            @Override
            public byte[] downloadReport(String reportId, String format) {
                logger.with("operation", "downloadReport")
                      .with("reportId", reportId)
                      .error("Report service unavailable", cause);
                
                return new byte[0];
            }
        };
    }
}