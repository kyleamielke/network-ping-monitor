package io.thatworked.support.search.domain.exception;

/**
 * Exception thrown when a search provider encounters an error.
 */
public class SearchProviderException extends SearchDomainException {
    
    public SearchProviderException(String message) {
        super(message);
    }
    
    public SearchProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}