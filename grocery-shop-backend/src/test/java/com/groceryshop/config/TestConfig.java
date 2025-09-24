package com.groceryshop.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test configuration for unit and integration tests.
 * Provides test-specific beans and overrides production configurations.
 */
@TestConfiguration
public class TestConfig {

    /**
     * Password encoder for testing - using a fixed strength for consistent test results.
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // Lower strength for faster tests
    }
}
