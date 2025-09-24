package com.groceryshop.config;

import io.restassured.RestAssured;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Global RestAssured configuration for integration tests.
 * Sets up base URI, port, and enables logging on failures.
 */
public class RestAssuredTestConfig implements TestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) {
        // Get the port from the test context
        Object testInstance = testContext.getTestInstance();
        if (testInstance != null) {
            try {
                var portField = testInstance.getClass().getDeclaredField("port");
                portField.setAccessible(true);
                int port = (Integer) portField.get(testInstance);

                // Configure RestAssured globally
                RestAssured.baseURI = "http://localhost";
                RestAssured.port = port;
                RestAssured.basePath = "/api";

                // Enable logging on failures
                RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

            } catch (Exception e) {
                // Fallback if port injection fails
                RestAssured.baseURI = "http://localhost";
                RestAssured.port = 8080; // Default port
                RestAssured.basePath = "/api";
            }
        }
    }

    @Override
    public void afterTestClass(TestContext testContext) {
        // Reset RestAssured configuration after tests
        RestAssured.reset();
    }
}
