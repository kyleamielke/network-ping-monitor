package io.thatworked.support.gateway.controller;

import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import io.thatworked.support.gateway.client.ReportServiceClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(originPatterns = "*")
public class ReportController {

    private final StructuredLogger logger;
    private final ReportServiceClient reportServiceClient;

    public ReportController(StructuredLoggerFactory loggerFactory, ReportServiceClient reportServiceClient) {
        this.logger = loggerFactory.getLogger(ReportController.class);
        this.reportServiceClient = reportServiceClient;
    }

    @GetMapping("/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable String reportId,
            @RequestParam(defaultValue = "PDF") String format) {
        
        logger.with("operation", "downloadReport")
              .with("reportId", reportId)
              .with("format", format)
              .info("Proxying report download request");

        try {
            byte[] reportData = reportServiceClient.downloadReport(reportId, format);
            
            if (reportData == null || reportData.length == 0) {
                return ResponseEntity.notFound().build();
            }

            ByteArrayResource resource = new ByteArrayResource(reportData);
            
            String filename = String.format("report_%s.%s", reportId, format.toLowerCase());
            MediaType contentType = format.equalsIgnoreCase("PDF") 
                ? MediaType.APPLICATION_PDF 
                : MediaType.parseMediaType("application/vnd.ms-excel");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(contentType)
                    .contentLength(reportData.length)
                    .body(resource);
                    
        } catch (Exception e) {
            logger.with("operation", "downloadReport")
                  .with("reportId", reportId)
                  .with("error", e.getMessage())
                  .error("Failed to download report", e);
            return ResponseEntity.notFound().build();
        }
    }
}