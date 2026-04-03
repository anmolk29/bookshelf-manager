package com.bookshelf.manager.dto;

import com.bookshelf.manager.model.Book;

/**
 * Stable JSON shape for clients (decouples API from JPA entity internals).
 */
public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        int totalCopies,
        int availableCopies,
        boolean availableToBorrow
) {
    public static BookResponse from(Book b) {
        return new BookResponse(
                b.getId(),
                b.getTitle(),
                b.getAuthor(),
                b.getIsbn(),
                b.getTotalCopies(),
                b.getAvailableCopies(),
                b.hasAvailableCopy()
        );
    }
}
