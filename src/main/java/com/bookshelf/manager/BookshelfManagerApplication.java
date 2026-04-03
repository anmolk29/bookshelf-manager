package com.bookshelf.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Spring Boot application.
 * <p>
 * {@code @SpringBootApplication} combines three things: configuration for "components"
 * (your {@code @Service}, {@code @Repository}, {@code @RestController} classes),
 * automatic Spring Boot setup, and component scanning in this package and sub-packages.
 */
@SpringBootApplication
public class BookshelfManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookshelfManagerApplication.class, args);
    }
}
