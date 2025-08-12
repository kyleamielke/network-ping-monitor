package io.thatworked.support.search.domain.exception;

/**
 * Exception thrown when a search query is invalid according to business rules.
 */
public class InvalidSearchQueryException extends SearchDomainException {
    
    public InvalidSearchQueryException(String message) {
        super(message);
    }
    
    public InvalidSearchQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}