package io.thatworked.support.gateway.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@FeignClient(
    name = "report-service",
    url = "${services.report.url:http://report-service:8085}",
    fallbackFactory = ReportServiceFallbackFactory.class
)
public interface ReportServiceClient {
    
    @PostMapping("/api/v1/reports")
    Map<String, Object> generateReport(@RequestBody Map<String, Object> request);
    
    @GetMapping("/api/v1/reports/{reportId}/download")
    byte[] downloadReport(@PathVariable("reportId") String reportId,
                         @RequestParam(defaultValue = "PDF") String format);
}