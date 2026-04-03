package com.bookshelf.manager.exception;

/**
 * Domain rule violation (for example, loaning a book with zero available copies).
 * Mapped to HTTP 400 by {@link GlobalExceptionHandler}.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
