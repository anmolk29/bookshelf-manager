package com.bookshelf.manager.controller;

import com.bookshelf.manager.dto.CheckoutRequest;
import com.bookshelf.manager.dto.LoanResponse;
import com.bookshelf.manager.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Lending endpoints: checkout creates a row and reduces inventory; return restores a copy.
 * Read endpoints expose active loans and overdue filtering for staff-style dashboards.
 */
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/active")
    public List<LoanResponse> active() {
        return loanService.activeLoans();
    }

    @GetMapping("/overdue")
    public List<LoanResponse> overdue() {
        return loanService.overdueLoans();
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse checkout(@Valid @RequestBody CheckoutRequest body) {
        return loanService.checkout(body);
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnBook(@PathVariable Long id) {
        return loanService.returnBook(id);
    }
}
