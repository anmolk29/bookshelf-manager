package com.bookshelf.manager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * JSON body for registering a new book in the catalog.
 */
public record CreateBookRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 120) String author,
        @NotBlank
        @Pattern(regexp = "[0-9Xx\\-]{10,17}", message = "ISBN must be 10-17 digits, dashes, or X")
        String isbn,
        @NotNull @Min(1) @Max(999) Integer totalCopies
) {
}
