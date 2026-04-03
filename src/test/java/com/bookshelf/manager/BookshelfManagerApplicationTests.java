package com.bookshelf.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Smoke test: ensures the Spring context starts (beans wire correctly).
 */
@SpringBootTest
@TestPropertySource(properties = "bookshelf.security.enabled=false")
class BookshelfManagerApplicationTests {

    @Test
    void contextLoads() {
    }
}
