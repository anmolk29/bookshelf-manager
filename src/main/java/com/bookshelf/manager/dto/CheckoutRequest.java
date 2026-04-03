package com.bookshelf.manager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Chooses which book and member participate in a loan, and how long the loan lasts.
 */
public record CheckoutRequest(
        @NotNull Long bookId,
        @NotNull Long memberId,
        @NotNull @Min(1) @Max(90) Integer loanDays
) {
}
