package com.bookshelf.manager.dto;

import com.bookshelf.manager.model.Loan;

import java.time.LocalDate;

/**
 * Read model for a loan row, including denormalized book/member labels for the UI.
 */
public record LoanResponse(
        Long id,
        Long bookId,
        String bookTitle,
        Long memberId,
        String memberName,
        LocalDate loanDate,
        LocalDate dueDate,
        LocalDate returnedAt,
        boolean active,
        boolean overdue
) {
    public static LoanResponse from(Loan loan, LocalDate today) {
        boolean active = loan.isActive();
        boolean overdue = active && loan.getDueDate().isBefore(today);
        return new LoanResponse(
                loan.getId(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.getMember().getId(),
                loan.getMember().getName(),
                loan.getLoanDate(),
                loan.getDueDate(),
                loan.getReturnedAt(),
                active,
                overdue
        );
    }
}
