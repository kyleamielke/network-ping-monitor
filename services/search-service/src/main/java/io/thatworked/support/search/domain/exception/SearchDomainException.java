package io.thatworked.support.search.domain.exception;

/**
 * Base exception for all domain-level exceptions in the search service.
 */
public class SearchDomainException extends RuntimeException {
    
    public SearchDomainException(String message) {
        super(message);
    }
    
    public SearchDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}