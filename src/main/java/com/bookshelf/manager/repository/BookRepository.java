package com.bookshelf.manager.repository;

import com.bookshelf.manager.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Data-access layer for {@link Book} entities.
 * Spring Data JPA implements this interface at runtime (no SQL in your code for simple queries).
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    /**
     * Case-insensitive partial match on title or author (good for a simple search bar).
     */
    @Query("""
            SELECT b FROM Book b
            WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(b.author) LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    List<Book> searchByTitleOrAuthor(@Param("q") String query);

    List<Book> findByAvailableCopiesGreaterThan(int minAvailable);

    List<Book> findByAvailableCopies(int availableCopies);
}
