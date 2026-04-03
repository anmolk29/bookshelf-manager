package com.bookshelf.manager.repository;

import com.bookshelf.manager.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Queries for loans, including overdue reporting.
 */
public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("""
            SELECT DISTINCT l FROM Loan l
            JOIN FETCH l.book JOIN FETCH l.member
            WHERE l.returnedAt IS NULL
            """)
    List<Loan> findActiveWithBookAndMember();

    /**
     * Active loans where due date is strictly before {@code asOf} (typically today).
     */
    @Query("""
            SELECT DISTINCT l FROM Loan l
            JOIN FETCH l.book JOIN FETCH l.member
            WHERE l.returnedAt IS NULL AND l.dueDate < :asOf
            """)
    List<Loan> findActiveOverdue(@Param("asOf") LocalDate asOf);

    @Query("""
            SELECT l FROM Loan l
            JOIN FETCH l.book JOIN FETCH l.member
            WHERE l.id = :id
            """)
    Optional<Loan> findByIdWithBookAndMember(@Param("id") Long id);
}
