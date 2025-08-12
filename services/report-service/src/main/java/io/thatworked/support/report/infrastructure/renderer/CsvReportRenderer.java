package io.thatworked.support.report.infrastructure.renderer;

import io.thatworked.support.report.domain.model.ReportDocument;
import io.thatworked.support.report.domain.port.ReportRenderer;
import org.springframework.stereotype.Component;

/**
 * Renders report documents as CSV files.
 */
@Component
public class CsvReportRenderer implements ReportRenderer {
    
    @Override
    public byte[] render(ReportDocument document) {
        StringBuilder csv = new StringBuilder();
        
        // Add title as comment
        csv.append("# ").append(document.title()).append("\n");
        if (document.subtitle() != null) {
            csv.append("# ").append(document.subtitle()).append("\n");
        }
        csv.append("# Generated: ").append(document.generatedAt()).append("\n");
        if (document.timeRange() != null) {
            csv.append("# Period: ").append(document.timeRange().getStartDate())
               .append(" to ").append(document.timeRange().getEndDate()).append("\n");
        }
        csv.append("\n");
        
        // Render each section
        for (var section : document.sections()) {
            if (section.type() == ReportDocument.SectionType.TABLE && !section.rows().isEmpty()) {
                // Add headers
                csv.append(String.join(",", section.headers().stream()
                    .map(this::escapeCSV)
                    .toList()));
                csv.append("\n");
                
                // Add rows
                for (var row : section.rows()) {
                    csv.append(String.join(",", row.cells().stream()
                        .map(cell -> escapeCSV(formatCell(cell)))
                        .toList()));
                    csv.append("\n");
                }
                csv.append("\n");
            } else if (section.type() == ReportDocument.SectionType.SUMMARY) {
                csv.append("# ").append(section.title()).append("\n");
                if (section.summary() != null) {
                    csv.append("# ").append(section.summary()).append("\n");
                }
                csv.append("\n");
            }
        }
        
        return csv.toString().getBytes();
    }
    
    @Override
    public String getMimeType() {
        return "text/csv";
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
    
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // Escape quotes and wrap in quotes if contains comma, quote, or newline
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}