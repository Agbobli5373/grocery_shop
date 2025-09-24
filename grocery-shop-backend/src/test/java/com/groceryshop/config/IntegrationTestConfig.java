package com.groceryshop.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Configuration for integration tests.
 * Provides beans that might be needed for integration testing.
 */
@TestConfiguration
public class IntegrationTestConfig {

    /**
     * TestRestTemplate configured for integration tests.
     * This ensures proper handling of HTTP requests in tests.
     */
    @Bean
    @Primary
    public TestRestTemplate testRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return new TestRestTemplate(restTemplateBuilder, null, null, TestRestTemplate.HttpClientOption.ENABLE_COOKIES);
    }

    // Configuration for integration tests can be added here if needed
}
