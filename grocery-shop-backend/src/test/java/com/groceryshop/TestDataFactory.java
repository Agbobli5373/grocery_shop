package com.groceryshop;

import com.groceryshop.auth.User;
import com.groceryshop.auth.UserRole;
import com.groceryshop.auth.UserStatus;
import com.groceryshop.cart.Cart;
import com.groceryshop.cart.CartItem;
import com.groceryshop.order.Order;
import com.groceryshop.order.OrderItem;
import com.groceryshop.order.OrderStatus;
import com.groceryshop.product.Product;
import com.groceryshop.product.ProductCategory;
import com.groceryshop.product.ProductStatus;
import com.groceryshop.shared.dto.request.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Factory class for creating test data objects.
 * Provides consistent test data creation across all test classes.
 */
public class TestDataFactory {

    // User Test Data
    public static User createTestUser() {
        return createTestUser(1L, "test@example.com", UserRole.CUSTOMER);
    }

    public static User createTestUser(Long id, String email, UserRole role) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPasswordHash("hashedPassword123");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

    public static User createTestAdmin() {
        return createTestUser(2L, "admin@example.com", UserRole.ADMIN);
    }

    // Product Test Data
    public static Product createTestProduct() {
        return createTestProduct(1L, "Test Product", ProductCategory.PANTRY);
    }

    public static Product createTestProduct(Long id, String name, ProductCategory category) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription("Test product description");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setImageUrl("https://example.com/image.jpg");
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);
        product.setStockQuantity(100);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    public static List<Product> createTestProducts() {
        return List.of(
            createTestProduct(1L, "Apple", ProductCategory.FRUITS),
            createTestProduct(2L, "Milk", ProductCategory.DAIRY),
            createTestProduct(3L, "Bread", ProductCategory.PANTRY)
        );
    }

    // Cart Test Data
    public static Cart createTestCart() {
        return createTestCart(1L, createTestUser());
    }

    public static Cart createTestCart(Long id, User user) {
        Cart cart = new Cart();
        cart.setId(id);
        cart.setCustomer(user);
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
        return cart;
    }

    public static CartItem createTestCartItem() {
        return createTestCartItem(1L, createTestCart(), createTestProduct());
    }

    public static CartItem createTestCartItem(Long id, Cart cart, Product product) {
        CartItem item = new CartItem();
        item.setId(id);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(product.getPrice());
        item.setAddedAt(LocalDateTime.now());
        return item;
    }

    // Order Test Data
    public static Order createTestOrder() {
        return createTestOrder(1L, createTestUser(), OrderStatus.PENDING);
    }

    public static Order createTestOrder(Long id, User user, OrderStatus status) {
        Order order = new Order();
        order.setId(id);
        order.setCustomer(user);
        order.setStatus(status);
        order.setTotalAmount(BigDecimal.valueOf(199.98));
        order.setDeliveryAddress("123 Test Street, Test City");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return order;
    }

    public static OrderItem createTestOrderItem() {
        return createTestOrderItem(1L, createTestOrder(), createTestProduct());
    }

    public static OrderItem createTestOrderItem(Long id, Order order, Product product) {
        OrderItem item = new OrderItem(product, 2, product.getPrice());
        item.setId(id);
        item.setOrder(order);
        return item;
    }

    // DTO Test Data
    public static RegisterRequest createTestRegisterRequest() {
        return new RegisterRequest("test@example.com", "password123", "Test", "User");
    }

    public static LoginRequest createTestLoginRequest() {
        return new LoginRequest("test@example.com", "password123");
    }

    public static CreateProductRequest createTestCreateProductRequest() {
        return new CreateProductRequest(
            "Test Product",
            "Description",
            BigDecimal.valueOf(99.99),
            "image.jpg",
            ProductCategory.PANTRY,
            100
        );
    }

    public static AddToCartRequest createTestAddToCartRequest() {
        return new AddToCartRequest(1L, 2);
    }

    public static CheckoutRequest createTestCheckoutRequest() {
        return new CheckoutRequest("123 Test Street, Test City");
    }

    // Utility Methods
    public static String generateTestJwtToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.signature";
    }

    public static String generateExpiredJwtToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.expired.signature";
    }
}
