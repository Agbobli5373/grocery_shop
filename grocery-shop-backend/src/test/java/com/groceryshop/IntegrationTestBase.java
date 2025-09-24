package com.groceryshop;

import com.groceryshop.config.IntegrationTestConfig;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Base class for integration tests.
 * Sets up Testcontainers for PostgreSQL and RabbitMQ.
 */
@SpringBootTest(
    classes = {GroceryShopApplication.class, IntegrationTestConfig.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@Testcontainers
public abstract class IntegrationTestBase {

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate restTemplate;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("grocery_test")
            .withUsername("test")
            .withPassword("test");

    // RabbitMQ disabled for tests to avoid connection issues
    // @Container
    // static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.12-management-alpine")
    //         .withAdminPassword("admin");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // RabbitMQ properties commented out since auto-configuration is excluded
        // registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        // registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        // registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        // registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }

    @BeforeAll
    static void setUp() {
        postgres.start();
        // rabbitMQ.start(); // Disabled for tests
    }

    /**
     * Helper method to build full URL for test requests.
     */
    protected String buildUrl(String path) {
        return "http://localhost:" + port + path;
    }
}
