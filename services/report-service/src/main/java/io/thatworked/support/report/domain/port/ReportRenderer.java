package io.thatworked.support.report.domain.port;

import io.thatworked.support.report.domain.model.ReportDocument;

/**
 * Port for rendering report documents into specific formats.
 */
public interface ReportRenderer {
    
    /**
     * Renders a report document into bytes for the specific format.
     */
    byte[] render(ReportDocument document);
    
    /**
     * Gets the MIME type for this renderer's output.
     */
    String getMimeType();
}