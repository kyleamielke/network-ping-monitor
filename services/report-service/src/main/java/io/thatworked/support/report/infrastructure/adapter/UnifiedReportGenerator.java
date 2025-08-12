package io.thatworked.support.report.infrastructure.adapter;

import io.thatworked.support.report.domain.model.*;
import io.thatworked.support.report.domain.port.ReportGeneratorPort;
import io.thatworked.support.report.domain.port.ReportRenderer;
import io.thatworked.support.report.domain.service.ReportDocumentBuilder;
import io.thatworked.support.report.infrastructure.renderer.CsvReportRenderer;
import io.thatworked.support.report.infrastructure.renderer.PdfReportRenderer;
import io.thatworked.support.common.logging.StructuredLogger;
import io.thatworked.support.common.logging.factory.StructuredLoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Unified report generator that follows DRY principles.
 * Uses a document builder to prepare data once, then renders it in the requested format.
 */
@Component
@Primary
public class UnifiedReportGenerator implements ReportGeneratorPort {
    
    private final ReportDocumentBuilder documentBuilder;
    private final Map<ReportFormat, ReportRenderer> renderers;
    private final StructuredLogger logger;
    
    public UnifiedReportGenerator(ReportDocumentBuilder documentBuilder,
                                 PdfReportRenderer pdfRenderer,
                                 CsvReportRenderer csvRenderer,
                                 StructuredLoggerFactory loggerFactory) {
        this.documentBuilder = documentBuilder;
        this.renderers = Map.of(
            ReportFormat.PDF, pdfRenderer,
            ReportFormat.CSV, csvRenderer
        );
        this.logger = loggerFactory.getLogger(UnifiedReportGenerator.class);
    }
    
    @Override
    public ReportContent generateReport(Report report, ReportData data) {
        logger.with("operation", "generateReport")
              .with("reportId", report.getId().toString())
              .with("format", report.getFormat().name())
              .with("reportType", report.getReportType().name())
              .info("Generating report using unified approach");
        
        try {
            // Step 1: Build the document structure (format-agnostic)
            ReportDocument document = documentBuilder.buildDocument(report, data);
            
            logger.with("operation", "generateReport")
                  .with("reportId", report.getId().toString())
                  .with("sections", document.sections().size())
                  .debug("Built report document structure");
            
            // Step 2: Render the document in the requested format
            ReportRenderer renderer = renderers.get(report.getFormat());
            if (renderer == null) {
                throw new IllegalArgumentException("Unsupported format: " + report.getFormat());
            }
            
            byte[] content = renderer.render(document);
            
            logger.with("operation", "generateReport")
                  .with("reportId", report.getId().toString())
                  .with("contentSize", content.length)
                  .info("Successfully generated report");
            
            return ReportContent.of(content);
            
        } catch (Exception e) {
            logger.with("operation", "generateReport")
                  .with("reportId", report.getId().toString())
                  .error("Failed to generate report", e);
            throw new RuntimeException("Report generation failed", e);
        }
    }
    
    @Override
    public boolean supports(ReportFormat format) {
        return renderers.containsKey(format);
    }
}