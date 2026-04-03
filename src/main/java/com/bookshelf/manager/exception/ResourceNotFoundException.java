package com.bookshelf.manager.exception;

/**
 * Thrown when a client references an ID that does not exist in the database.
 * Mapped to HTTP 404 by {@link GlobalExceptionHandler}.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
