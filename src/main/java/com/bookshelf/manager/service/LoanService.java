package com.bookshelf.manager.service;

import com.bookshelf.manager.dto.CheckoutRequest;
import com.bookshelf.manager.dto.LoanResponse;
import com.bookshelf.manager.exception.BusinessRuleException;
import com.bookshelf.manager.exception.ResourceNotFoundException;
import com.bookshelf.manager.model.Book;
import com.bookshelf.manager.model.Loan;
import com.bookshelf.manager.model.Member;
import com.bookshelf.manager.repository.BookRepository;
import com.bookshelf.manager.repository.LoanRepository;
import com.bookshelf.manager.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

/**
 * Core lending workflow: checkout decreases inventory; return restores a copy.
 * Overdue logic compares {@code dueDate} to "today" from a {@link Clock} (easy to test / reason about).
 */
@Service
public class LoanService {

    private final LoanRepository loans;
    private final BookRepository books;
    private final MemberRepository members;
    private final Clock clock;

    public LoanService(LoanRepository loans, BookRepository books, MemberRepository members, Clock clock) {
        this.loans = loans;
        this.books = books;
        this.members = members;
        this.clock = clock;
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> activeLoans() {
        LocalDate today = LocalDate.now(clock);
        return loans.findActiveWithBookAndMember().stream()
                .map(l -> LoanResponse.from(l, today))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> overdueLoans() {
        LocalDate today = LocalDate.now(clock);
        return loans.findActiveOverdue(today).stream()
                .map(l -> LoanResponse.from(l, today))
                .toList();
    }

    /**
     * Creates a loan row and decrements {@code availableCopies} when inventory allows it.
     */
    @Transactional
    public LoanResponse checkout(CheckoutRequest req) {
        Book book = books.findById(req.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + req.bookId()));
        Member member = members.findById(req.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + req.memberId()));

        if (!book.hasAvailableCopy()) {
            throw new BusinessRuleException("No copies available to loan for book id " + book.getId());
        }

        LocalDate today = LocalDate.now(clock);
        LocalDate due = today.plusDays(req.loanDays());

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        books.save(book);

        Loan loan = new Loan(book, member, today, due);
        Loan saved = loans.save(loan);
        // Re-fetch with associations for a stable response (ID is assigned after flush)
        Loan hydrated = loans.findByIdWithBookAndMember(saved.getId())
                .orElseThrow(() -> new IllegalStateException("Loan not readable after save"));
        return LoanResponse.from(hydrated, today);
    }

    /**
     * Marks a loan returned and puts one copy back on the shelf.
     */
    @Transactional
    public LoanResponse returnBook(Long loanId) {
        LocalDate today = LocalDate.now(clock);
        Loan loan = loans.findByIdWithBookAndMember(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found: " + loanId));
        if (!loan.isActive()) {
            throw new BusinessRuleException("Loan " + loanId + " is already returned");
        }
        Book book = loan.getBook();
        if (book.getAvailableCopies() >= book.getTotalCopies()) {
            throw new BusinessRuleException("Inventory invariant broken for book " + book.getId());
        }
        loan.setReturnedAt(today);
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        loans.save(loan);
        books.save(book);
        Loan hydrated = loans.findByIdWithBookAndMember(loanId)
                .orElseThrow(() -> new IllegalStateException("Loan not readable after return"));
        return LoanResponse.from(hydrated, today);
    }
}
