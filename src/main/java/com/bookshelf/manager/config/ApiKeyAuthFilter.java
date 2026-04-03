package com.bookshelf.manager.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lightweight "pretend authentication": mutating endpoints require a matching API key header.
 * This is not real security (the key is in config), but it demonstrates filters and how APIs gate writes.
 */
@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final String HEADER = "X-API-Key";

    private final BookshelfSecurityProperties security;

    public ApiKeyAuthFilter(BookshelfSecurityProperties security) {
        this.security = security;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        if (!security.isEnabled()) {
            return true;
        }
        String method = request.getMethod();
        return HttpMethod.GET.matches(method)
                || HttpMethod.OPTIONS.matches(method)
                || HttpMethod.HEAD.matches(method);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String provided = request.getHeader(HEADER);
        String expected = security.getApiKey();
        if (expected == null || expected.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }
        if (provided != null && constantTimeEquals(provided, expected)) {
            filterChain.doFilter(request, response);
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"code\":\"UNAUTHORIZED\",\"message\":\"Missing or invalid X-API-Key header\"}");
    }

    /** Very small timing-attack hardening; good habit even for demo keys. */
    private boolean constantTimeEquals(String a, String b) {
        byte[] x = a.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] y = b.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (x.length != y.length) {
            return false;
        }
        int r = 0;
        for (int i = 0; i < x.length; i++) {
            r |= x[i] ^ y[i];
        }
        return r == 0;
    }
}
