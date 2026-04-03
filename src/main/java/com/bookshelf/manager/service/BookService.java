package com.bookshelf.manager.service;

import com.bookshelf.manager.dto.BookResponse;
import com.bookshelf.manager.dto.CreateBookRequest;
import com.bookshelf.manager.exception.BusinessRuleException;
import com.bookshelf.manager.exception.ResourceNotFoundException;
import com.bookshelf.manager.model.Book;
import com.bookshelf.manager.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for the catalog: creating books, searching, and reporting availability.
 * Controllers call this class instead of talking to {@link BookRepository} directly.
 */
@Service
public class BookService {

    private final BookRepository books;

    public BookService(BookRepository books) {
        this.books = books;
    }

    @Transactional(readOnly = true)
    public List<BookResponse> findAll() {
        return books.findAll().stream().map(BookResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public BookResponse getById(Long id) {
        Book b = books.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + id));
        return BookResponse.from(b);
    }

    /**
     * Free-text search across title and author (case-insensitive).
     */
    @Transactional(readOnly = true)
    public List<BookResponse> search(String query) {
        if (query == null || query.isBlank()) {
            return findAll();
        }
        return books.searchByTitleOrAuthor(query.trim()).stream().map(BookResponse::from).toList();
    }

    /**
     * Optional filter: only titles that currently have at least one copy on the shelf.
     */
    @Transactional(readOnly = true)
    public List<BookResponse> listBorrowableOnly() {
        return books.findByAvailableCopiesGreaterThan(0).stream().map(BookResponse::from).toList();
    }

    @Transactional
    public BookResponse create(CreateBookRequest req) {
        String normalizedIsbn = normalizeIsbn(req.isbn());
        if (books.existsByIsbn(normalizedIsbn)) {
            throw new BusinessRuleException("A book with ISBN " + normalizedIsbn + " already exists");
        }
        if (req.totalCopies() < 1) {
            throw new BusinessRuleException("totalCopies must be at least 1");
        }
        Book book = new Book(req.title().trim(), req.author().trim(), normalizedIsbn, req.totalCopies());
        return BookResponse.from(books.save(book));
    }

    private static String normalizeIsbn(String raw) {
        return raw.replace("-", "").trim();
    }
}
