package com.groceryshop;

import com.groceryshop.product.Product;
import com.groceryshop.product.ProductCategory;
import com.groceryshop.product.ProductRepository;
import com.groceryshop.product.ProductStatus;
import com.groceryshop.shared.dto.request.*;
import com.groceryshop.shared.dto.response.AuthResponse;
import com.groceryshop.shared.dto.response.CartResponse;
import com.groceryshop.shared.dto.response.OrderResponse;
import com.groceryshop.shared.dto.response.ProductResponse;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Comprehensive API test suite covering all endpoints and scenarios.
 * Tests authentication, products, cart, orders, admin functions, and error handling.
 */
@DisplayName("Comprehensive API Test Suite")
class ComprehensiveApiTest extends IntegrationTestBase {

    @Autowired
    private ProductRepository productRepository;

    private String userToken;
    private String adminToken;
    private Long testProductId;
    private Long testCartId;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.basePath = "/api";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Ensure seed data exists
        ensureSeedData();

        // Get authentication tokens
        userToken = getUserToken();
        adminToken = getAdminToken();
    }

    private void ensureSeedData() {
        var dbProducts = productRepository.findAll();
        if (dbProducts.isEmpty()) {
            insertSeedProducts();
        }
        // Get first product ID for testing
        testProductId = productRepository.findAll().get(0).getId();
    }

    private void insertSeedProducts() {
        LocalDateTime now = LocalDateTime.now();

        Product apple = new Product();
        apple.setName("Apple");
        apple.setDescription("Fresh red apple");
        apple.setPrice(new BigDecimal("2.50"));
        apple.setStockQuantity(100);
        apple.setCategory(ProductCategory.FRUITS);
        apple.setStatus(ProductStatus.ACTIVE);
        apple.setCreatedAt(now);
        apple.setUpdatedAt(now);

        Product banana = new Product();
        banana.setName("Banana");
        banana.setDescription("Yellow banana");
        banana.setPrice(new BigDecimal("1.20"));
        banana.setStockQuantity(150);
        banana.setCategory(ProductCategory.FRUITS);
        banana.setStatus(ProductStatus.ACTIVE);
        banana.setCreatedAt(now);
        banana.setUpdatedAt(now);

        Product milk = new Product();
        milk.setName("Milk");
        milk.setDescription("Whole milk 1L");
        milk.setPrice(new BigDecimal("3.50"));
        milk.setStockQuantity(50);
        milk.setCategory(ProductCategory.DAIRY);
        milk.setStatus(ProductStatus.ACTIVE);
        milk.setCreatedAt(now);
        milk.setUpdatedAt(now);

        Product bread = new Product();
        bread.setName("Bread");
        bread.setDescription("Whole grain bread");
        bread.setPrice(new BigDecimal("2.00"));
        bread.setStockQuantity(75);
        bread.setCategory(ProductCategory.PANTRY);
        bread.setStatus(ProductStatus.ACTIVE);
        bread.setCreatedAt(now);
        bread.setUpdatedAt(now);

        Product orangeJuice = new Product();
        orangeJuice.setName("Orange Juice");
        orangeJuice.setDescription("Fresh orange juice 500ml");
        orangeJuice.setPrice(new BigDecimal("4.00"));
        orangeJuice.setStockQuantity(30);
        orangeJuice.setCategory(ProductCategory.BEVERAGES);
        orangeJuice.setStatus(ProductStatus.ACTIVE);
        orangeJuice.setCreatedAt(now);
        orangeJuice.setUpdatedAt(now);

        productRepository.saveAll(java.util.List.of(apple, banana, milk, bread, orangeJuice));
    }

    private String getUserToken() {
        RegisterRequest registerRequest = new RegisterRequest(
            "testuser@example.com",
            "testpass123",
            "Test",
            "User"
        );

        // Try to register first (ignore if user exists)
        given()
            .contentType("application/json")
            .body(registerRequest)
            .when()
            .post("/auth/register")
            .then()
            .statusCode(anyOf(is(201), is(500))); // 500 for duplicate registration

        // Login to get token
        LoginRequest loginRequest = new LoginRequest(
            "testuser@example.com",
            "testpass123"
        );

        AuthResponse authResponse = given()
            .contentType("application/json")
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract().as(AuthResponse.class);

        return authResponse.token();
    }

    private String getAdminToken() {
        LoginRequest loginRequest = new LoginRequest(
            "admin@groceryshop.com",
            "admin123"
        );

        AuthResponse authResponse = given()
            .contentType("application/json")
            .body(loginRequest)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract().as(AuthResponse.class);

        return authResponse.token();
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("User registration with valid data")
        void userRegistration_ValidData_ShouldSucceed() {
            RegisterRequest request = new RegisterRequest(
                "newuser@example.com",
                "password123",
                "New",
                "User"
            );

            given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201)
                .body("email", equalTo("newuser@example.com"))
                .body("firstName", equalTo("New"))
                .body("lastName", equalTo("User"))
                .body("role", equalTo("CUSTOMER"));
        }

        @Test
        @DisplayName("User login with valid credentials")
        void userLogin_ValidCredentials_ShouldReturnToken() {
            LoginRequest request = new LoginRequest(
                "testuser@example.com",
                "testpass123"
            );

            given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("refreshToken", notNullValue())
                .body("email", equalTo("testuser@example.com"));
        }

        @Test
        @DisplayName("Get current user with valid token")
        void getCurrentUser_ValidToken_ShouldReturnUser() {
            given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/auth/me")
                .then()
                .statusCode(200)
                .body("email", equalTo("testuser@example.com"))
                .body("firstName", equalTo("Test"))
                .body("lastName", equalTo("User"));
        }

        @Test
        @DisplayName("Validate token")
        void validateToken_ValidToken_ShouldSucceed() {
            given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .post("/auth/validate")
                .then()
                .statusCode(200);
        }

        @Test
        @DisplayName("Logout user")
        void logoutUser_ShouldSucceed() {
            given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .post("/auth/logout")
                .then()
                .statusCode(200);
        }
    }

    @Nested
    @DisplayName("Product Management Tests")
    class ProductTests {

        @Test
        @DisplayName("Get all products")
        void getAllProducts_ShouldReturnProducts() {
            ProductResponse[] products = given()
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .extract().as(ProductResponse[].class);

            assertThat(products).isNotNull();
            assertThat(products.length).isGreaterThanOrEqualTo(5);
            assertThat(products).anyMatch(p -> "Apple".equals(p.name()));
            assertThat(products).anyMatch(p -> "Banana".equals(p.name()));
        }

        @Test
        @DisplayName("Get product by ID")
        void getProductById_ValidId_ShouldReturnProduct() {
            given()
                .pathParam("id", testProductId)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(testProductId.intValue()))
                .body("name", notNullValue())
                .body("price", notNullValue());
        }

        @Test
        @DisplayName("Search products")
        void searchProducts_ValidQuery_ShouldReturnFilteredResults() {
            given()
                .queryParam("query", "apple")
                .when()
                .get("/products/search")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
        }

        @Test
        @DisplayName("Get products by category")
        void getProductsByCategory_ValidCategory_ShouldReturnProducts() {
            given()
                .pathParam("category", "FRUITS")
                .when()
                .get("/products/category/{category}")
                .then()
                .statusCode(200);
        }

        @Test
        @DisplayName("Create product as admin")
        void createProduct_AsAdmin_ShouldSucceed() {
            CreateProductRequest request = new CreateProductRequest(
                "Test Product",
                "Test description",
                new BigDecimal("10.00"),
                null, // imageUrl
                ProductCategory.PANTRY,
                25
            );

            given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .body("name", equalTo("Test Product"))
                .body("price", equalTo(10.00f));
        }

        @Test
        @DisplayName("Update product as admin")
        void updateProduct_AsAdmin_ShouldSucceed() {
            UpdateProductRequest request = new UpdateProductRequest(
                "Updated Apple",
                "Updated description",
                new BigDecimal("3.00"),
                null, // imageUrl
                ProductCategory.FRUITS,
                ProductStatus.ACTIVE,
                120
            );

            given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(request)
                .pathParam("id", testProductId)
                .when()
                .put("/products/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Updated Apple"));
        }

        @Test
        @DisplayName("Delete product as admin")
        void deleteProduct_AsAdmin_ShouldSucceed() {
            // First create a product to delete
            CreateProductRequest createRequest = new CreateProductRequest(
                "Product to Delete",
                "Will be deleted",
                new BigDecimal("5.00"),
                null, // imageUrl
                ProductCategory.PANTRY,
                10
            );

            ProductResponse createdProduct = given()
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json")
                .body(createRequest)
                .when()
                .post("/products")
                .then()
                .statusCode(201)
                .extract().as(ProductResponse.class);

            // Now delete it
            given()
                .header("Authorization", "Bearer " + adminToken)
                .pathParam("id", createdProduct.id())
                .when()
                .delete("/products/{id}")
                .then()
                .statusCode(204);
        }
    }

    @Nested
    @DisplayName("Shopping Cart Tests")
    class CartTests {

        @Test
        @DisplayName("Add item to cart")
        void addItemToCart_ValidRequest_ShouldSucceed() {
            AddToCartRequest request = new AddToCartRequest(testProductId, 2);

            CartResponse response = given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/cart/items")
                .then()
                .statusCode(200)
                .extract().as(CartResponse.class);

            assertThat(response).isNotNull();
            assertThat(response.items()).anyMatch(item -> item.productId().equals(testProductId));
            testCartId = response.id();
        }

        @Test
        @DisplayName("Get cart contents")
        void getCartContents_ShouldReturnCart() {
            given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/cart")
                .then()
                .statusCode(200)
                .body("items", notNullValue());
        }

        @Test
        @DisplayName("Update cart item quantity")
        void updateCartItemQuantity_ValidRequest_ShouldSucceed() {
            // First add an item
            AddToCartRequest addRequest = new AddToCartRequest(testProductId, 1);

            CartResponse cart = given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(addRequest)
                .when()
                .post("/cart/items")
                .then()
                .statusCode(200)
                .extract().as(CartResponse.class);

            // Update quantity
            UpdateCartItemRequest updateRequest = new UpdateCartItemRequest(3);

            given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(updateRequest)
                .pathParam("productId", testProductId)
                .when()
                .put("/cart/items/{productId}")
                .then()
                .statusCode(200);
        }

        @Test
        @DisplayName("Remove item from cart")
        void removeItemFromCart_ShouldSucceed() {
            // First add an item
            AddToCartRequest addRequest = new AddToCartRequest(testProductId, 1);

            given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(addRequest)
                .when()
                .post("/cart/items")
                .then()
                .statusCode(200);

            // Remove the item
            given()
                .header("Authorization", "Bearer " + userToken)
                .pathParam("productId", testProductId)
                .when()
                .delete("/cart/items/{productId}")
                .then()
                .statusCode(204);
        }
    }

    @Nested
    @DisplayName("Order Management Tests")
    class OrderTests {

        @Test
        @DisplayName("Create order (checkout)")
        void createOrder_ValidCart_ShouldSucceed() {
            // First add items to cart
            AddToCartRequest addRequest = new AddToCartRequest(testProductId, 1);

            given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(addRequest)
                .when()
                .post("/cart/items")
                .then()
                .statusCode(200);

            // Checkout
            CheckoutRequest checkoutRequest = new CheckoutRequest("123 Test Street");

            OrderResponse order = given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(checkoutRequest)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .extract().as(OrderResponse.class);

            assertThat(order).isNotNull();
            assertThat(order.id()).isNotNull();
        }

        @Test
        @DisplayName("Get user orders")
        void getUserOrders_ShouldReturnOrders() {
            given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get("/orders")
                .then()
                .statusCode(200);
        }

        @Test
        @DisplayName("Get order by ID")
        void getOrderById_ValidId_ShouldReturnOrder() {
            // First create an order
            AddToCartRequest addRequest = new AddToCartRequest(testProductId, 1);

            given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(addRequest)
                .when()
                .post("/cart/items")
                .then()
                .statusCode(200);

            CheckoutRequest checkoutRequest = new CheckoutRequest("123 Test Street");

            OrderResponse createdOrder = given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(checkoutRequest)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .extract().as(OrderResponse.class);

            // Get the order by ID
            given()
                .header("Authorization", "Bearer " + userToken)
                .pathParam("id", createdOrder.id())
                .when()
                .get("/orders/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(createdOrder.id().intValue()));
        }
    }

    @Nested
    @DisplayName("Recommendation Tests")
    class RecommendationTests {

        @Test
        @DisplayName("Get product recommendations")
        void getProductRecommendations_ShouldReturnRecommendations() {
            given()
                .when()
                .get("/products/recommendations")
                .then()
                .statusCode(200);
        }
    }

    @Nested
    @DisplayName("Admin Tests")
    class AdminTests {

        @Test
        @DisplayName("Get dashboard statistics")
        void getDashboardStatistics_AsAdmin_ShouldSucceed() {
            given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get("/admin/dashboard")
                .then()
                .statusCode(200);
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Access protected endpoint without token")
        void accessProtectedEndpoint_WithoutToken_ShouldReturn401() {
            given()
                .when()
                .get("/auth/me")
                .then()
                .statusCode(401);
        }

        @Test
        @DisplayName("Access non-existent resource")
        void accessNonExistentResource_ShouldReturn404() {
            given()
                .pathParam("id", 99999)
                .when()
                .get("/products/{id}")
                .then()
                .statusCode(404);
        }

        @Test
        @DisplayName("Create product without admin role")
        void createProduct_WithoutAdminRole_ShouldReturn403() {
            CreateProductRequest request = new CreateProductRequest(
                "Unauthorized Product",
                "Should fail",
                new BigDecimal("10.00"),
                null, // imageUrl
                ProductCategory.PANTRY,
                25
            );

            given()
                .header("Authorization", "Bearer " + userToken)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/products")
                .then()
                .statusCode(403);
        }
    }
}
