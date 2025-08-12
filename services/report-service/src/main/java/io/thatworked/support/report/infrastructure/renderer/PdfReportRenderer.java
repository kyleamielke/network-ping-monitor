package io.thatworked.support.report.infrastructure.renderer;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import io.thatworked.support.report.domain.model.ReportDocument;
import io.thatworked.support.report.domain.port.ReportRenderer;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Renders report documents as PDF files.
 */
@Component
public class PdfReportRenderer implements ReportRenderer {
    
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public byte[] render(ReportDocument reportDocument) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Add title
            document.add(new Paragraph(reportDocument.title())
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18)
                .setBold());
            
            // Add subtitle
            if (reportDocument.subtitle() != null) {
                document.add(new Paragraph(reportDocument.subtitle())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14));
            }
            
            // Add metadata
            document.add(new Paragraph("Generated: " + DISPLAY_FORMAT.format(reportDocument.generatedAt().atOffset(ZoneOffset.UTC)))
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10));
            
            if (reportDocument.timeRange() != null) {
                document.add(new Paragraph(
                    String.format("Period: %s to %s", 
                        DISPLAY_FORMAT.format(reportDocument.timeRange().getStartDate().atOffset(ZoneOffset.UTC)),
                        DISPLAY_FORMAT.format(reportDocument.timeRange().getEndDate().atOffset(ZoneOffset.UTC))))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(10));
            }
            
            document.add(new Paragraph("\n"));
            
            // Render sections
            for (var section : reportDocument.sections()) {
                if (section.title() != null) {
                    document.add(new Paragraph(section.title()).setBold().setFontSize(14));
                }
                
                switch (section.type()) {
                    case TABLE -> renderTable(document, section);
                    case SUMMARY -> renderSummary(document, section);
                    case CHART_DATA -> { /* Could add chart rendering here */ }
                }
                
                document.add(new Paragraph("\n"));
            }
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to render PDF", e);
        }
    }
    
    @Override
    public String getMimeType() {
        return "application/pdf";
    }
    
    private void renderTable(Document document, ReportDocument.Section section) {
        if (section.rows().isEmpty()) {
            if (section.summary() != null) {
                document.add(new Paragraph(section.summary()));
            }
            return;
        }
        
        // Create table with dynamic column widths
        float[] columnWidths = new float[section.headers().size()];
        for (int i = 0; i < columnWidths.length; i++) {
            columnWidths[i] = 1f; // Equal width for simplicity
        }
        
        Table table = new Table(UnitValue.createPercentArray(columnWidths));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Add headers
        for (String header : section.headers()) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
        }
        
        // Add data rows
        for (var row : section.rows()) {
            for (var cell : row.cells()) {
                table.addCell(formatCell(cell));
            }
        }
        
        document.add(table);
    }
    
    private void renderSummary(Document document, ReportDocument.Section section) {
        if (section.summary() != null) {
            document.add(new Paragraph(section.summary()));
        }
    }
    
    private String formatCell(ReportDocument.Cell cell) {
        if (cell.value() == null) {
            return "";
        }
        
        return switch (cell.type()) {
            case PERCENTAGE -> {
                try {
                    double value = Double.parseDouble(cell.value());
                    yield String.format("%.2f%%", value);
                } catch (NumberFormatException e) {
                    yield cell.value();
                }
            }
            case NUMBER -> {
                if (cell.format() != null && cell.format().contains(".")) {
                    try {
                        double value = Double.parseDouble(cell.value());
                        yield String.format("%.2f", value);
                    } catch (NumberFormatException e) {
                        yield cell.value();
                    }
                }
                yield cell.value();
            }
            default -> cell.value();
        };
    }
}