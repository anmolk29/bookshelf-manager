package com.bookshelf.manager.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Binds {@code bookshelf.security.*} from application.properties.
 * Lets you turn API-key checks off for local experiments without deleting code.
 */
@ConfigurationProperties(prefix = "bookshelf.security")
public class BookshelfSecurityProperties {

    /** When false, all requests are allowed (useful for quick manual testing). */
    private boolean enabled = true;

    /** Shared secret clients send as {@code X-API-Key} on mutating HTTP calls. */
    private String apiKey = "demo-secret";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
