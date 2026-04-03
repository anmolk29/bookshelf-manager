package com.bookshelf.manager.controller;

import com.bookshelf.manager.dto.BookResponse;
import com.bookshelf.manager.dto.CreateBookRequest;
import com.bookshelf.manager.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * HTTP API for catalog operations: list, search, filter borrowable titles, create books.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * List books. Use {@code borrowableOnly=true} to hide titles with zero free copies.
     * Use {@code q=} for a simple title/author search.
     */
    @GetMapping
    public List<BookResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(name = "borrowableOnly", defaultValue = "false") boolean borrowableOnly
    ) {
        List<BookResponse> books = borrowableOnly
                ? bookService.listBorrowableOnly()
                : bookService.search(q);
        if (borrowableOnly && q != null && !q.isBlank()) {
            String needle = q.trim().toLowerCase();
            return books.stream()
                    .filter(b -> b.title().toLowerCase().contains(needle)
                            || b.author().toLowerCase().contains(needle))
                    .toList();
        }
        return books;
    }

    @GetMapping("/{id}")
    public BookResponse byId(@PathVariable Long id) {
        return bookService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookResponse create(@Valid @RequestBody CreateBookRequest body) {
        return bookService.create(body);
    }
}
