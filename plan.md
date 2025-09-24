# Grocery Shop Backend - Event-Driven Architecture Plan

## Overview
This document outlines the implementation plan for a self-contained Spring Boot backend service for a Picnic-inspired grocery online shop. The system uses an event-driven architecture with RabbitMQ for asynchronous communication and follows Domain-Driven Design (DDD) principles.

## Architecture Overview

### Core Principles
- **Modular Monolith**: Using Spring Modulith for domain-driven modular architecture
- **Domain Ownership**: Each domain owns its complete bounded context (entities, services, events, APIs)
- **Event-Driven**: All business operations trigger events published to RabbitMQ
- **CQRS Pattern**: Separate read/write models for complex operations
- **Saga Pattern**: Distributed transactions for order fulfillment

### Technology Stack
- **Framework**: Spring Boot 3.5.0 with Spring Modulith
- **Language**: Java 24 (latest LTS with enhanced process API, improved collections, and modern concurrency features)
- **Database**: H2 (dev) / PostgreSQL (prod) with Flyway migrations
- **Message Broker**: RabbitMQ with Spring AMQP
- **Security**: Spring Security with JWT
- **Modularity**: Spring Modulith for domain-driven architecture
- **Documentation**: OpenAPI/Swagger

### Java 24 Features to Leverage
- **Enhanced Process API**: Better process management for system integrations
- **Improved Collections**: More efficient data structures for product catalogs and order processing
- **Virtual Threads**: Lightweight concurrency for handling multiple simultaneous orders
- **Pattern Matching**: Cleaner code for event type handling and data validation
- **Records**: Immutable data classes for event payloads and DTOs

## Spring Modulith Project Structure

```
grocery-shop-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/groceryshop/
│   │   │       ├── GroceryShopApplication.java
│   │   │       ├── config/                          # Shared configuration
│   │   │       │   ├── RabbitMQConfig.java
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   ├── CorsConfig.java
│   │   │       │   └── SseConfig.java
│   │   │       ├── auth/                           # 🔐 Auth Module
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── User.java
│   │   │       │   ├── UserRepository.java
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── UserRegisteredEvent.java
│   │   │       │   ├── UserEventHandler.java
│   │   │       │   ├── spi/                        # Service Provider Interface
│   │   │       │   │   └── AuthServiceProvider.java
│   │   │       │   └── internal/                   # Internal implementation
│   │   │       │       ├── JwtUtil.java
│   │   │       │       └── PasswordEncoderUtil.java
│   │   │       ├── product/                        # 📦 Product Module
│   │   │       │   ├── ProductController.java
│   │   │       │   ├── Product.java
│   │   │       │   ├── ProductRepository.java
│   │   │       │   ├── ProductService.java
│   │   │       │   ├── ProductAddedEvent.java
│   │   │       │   ├── ProductUpdatedEvent.java
│   │   │       │   ├── InventoryService.java
│   │   │       │   ├── StockUpdatedEvent.java
│   │   │       │   ├── LowStockAlertEvent.java
│   │   │       │   ├── spi/
│   │   │       │   │   ├── ProductServiceProvider.java
│   │   │       │   │   └── InventoryServiceProvider.java
│   │   │       │   └── internal/
│   │   │       │       ├── ProductSearchCriteria.java
│   │   │       │       └── InventoryForecast.java
│   │   │       ├── cart/                          # 🛒 Cart Module
│   │   │       │   ├── CartController.java
│   │   │       │   ├── Cart.java
│   │   │       │   ├── CartItem.java
│   │   │       │   ├── CartRepository.java
│   │   │       │   ├── CartItemRepository.java
│   │   │       │   ├── CartService.java
│   │   │       │   ├── ItemAddedToCartEvent.java
│   │   │       │   ├── ItemRemovedFromCartEvent.java
│   │   │       │   ├── CartCheckedOutEvent.java
│   │   │       │   ├── spi/
│   │   │       │   │   └── CartServiceProvider.java
│   │   │       │   └── internal/
│   │   │       │       └── CartValidator.java
│   │   │       ├── order/                         # 📋 Order Module
│   │   │       │   ├── OrderController.java
│   │   │       │   ├── Order.java
│   │   │       │   ├── OrderItem.java
│   │   │       │   ├── OrderRepository.java
│   │   │       │   ├── OrderItemRepository.java
│   │   │       │   ├── OrderService.java
│   │   │       │   ├── OrderCreatedEvent.java
│   │   │       │   ├── OrderStatusUpdatedEvent.java
│   │   │       │   ├── OrderEventHandler.java
│   │   │       │   ├── spi/
│   │   │       │   │   └── OrderServiceProvider.java
│   │   │       │   └── internal/
│   │   │       │       ├── OrderSagaManager.java
│   │   │       │       └── OrderStatusTransition.java
│   │   │       ├── notification/                  # 📢 Notification Module
│   │   │       │   ├── NotificationService.java
│   │   │       │   ├── EmailNotificationService.java
│   │   │       │   ├── PushNotificationService.java
│   │   │       │   ├── NotificationEventHandler.java
│   │   │       │   ├── spi/
│   │   │       │   │   └── NotificationServiceProvider.java
│   │   │       │   └── internal/
│   │   │       │       ├── EmailTemplate.java
│   │   │       │       └── NotificationQueue.java
│   │   │       ├── admin/                         # 👨‍💼 Admin Module
│   │   │       │   ├── AdminController.java
│   │   │       │   ├── AdminDashboardService.java
│   │   │       │   ├── AnalyticsService.java
│   │   │       │   ├── spi/
│   │   │       │   │   └── AdminServiceProvider.java
│   │   │       │   └── internal/
│   │   │       │       ├── DashboardMetrics.java
│   │   │       │       └── AnalyticsAggregator.java
│   │   │       ├── shared/                        # 🔗 Shared Infrastructure
│   │   │       │   ├── dto/
│   │   │       │   │   ├── request/
│   │   │       │   │   │   ├── LoginRequest.java
│   │   │       │   │   │   ├── RegisterRequest.java
│   │   │       │   │   │   ├── CreateOrderRequest.java
│   │   │       │   │   │   └── UpdateProductRequest.java
│   │   │       │   │   └── response/
│   │   │       │   │       ├── AuthResponse.java
│   │   │       │   │       ├── ProductResponse.java
│   │   │       │   │       ├── OrderResponse.java
│   │   │       │   │       └── CartResponse.java
│   │   │       │   ├── exception/
│   │   │       │   │   ├── GlobalExceptionHandler.java
│   │   │       │   │   ├── ResourceNotFoundException.java
│   │   │       │   │   └── ValidationException.java
│   │   │       │   └── util/
│   │   │       │       └── SseEmitterUtil.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/migration/
│   │           ├── V1__create_users_table.sql
│   │           ├── V2__create_products_table.sql
│   │           ├── V3__create_orders_and_items_tables.sql
│   │           ├── V4__create_cart_and_items_tables.sql
│   │           └── V5__create_inventory_tracking.sql
│   └── test/
│       ├── java/
│       │   └── com/groceryshop/
│       │       ├── ModularityTests.java         # Spring Modulith verification
│       │       ├── auth/
│       │       │   └── AuthModuleIntegrationTests.java
│       │       ├── product/
│       │       │   └── ProductModuleIntegrationTests.java
│       │       ├── cart/
│       │       │   └── CartModuleIntegrationTests.java
│       │       ├── order/
│       │       │   └── OrderModuleIntegrationTests.java
│       │       └── shared/
│       └── resources/
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── docs/
│   ├── api/
│   └── architecture/
├── pom.xml
├── README.md
└── .gitignore
```

### Application Module Annotations

```java
// Main Application
@SpringBootApplication
@EnableJpaRepositories
@EnableModulith
public class GroceryShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(GroceryShopApplication.class, args);
    }
}

// Auth Module (package-info.java)
@ApplicationModule(
    allowedDependencies = {}  // Independent module
)
package com.groceryshop.auth;

// Product Module
@ApplicationModule(
    allowedDependencies = {"auth"}  // Can use auth services
)
package com.groceryshop.product;

// Cart Module
@ApplicationModule(
    allowedDependencies = {"auth", "product"}
)
package com.groceryshop.cart;

// Order Module
@ApplicationModule(
    allowedDependencies = {"auth", "product", "cart", "notification"}
)
package com.groceryshop.order;

// Notification Module
@ApplicationModule(
    allowedDependencies = {}  // Independent, event-driven
)
package com.groceryshop.notification;

// Admin Module
@ApplicationModule(
    allowedDependencies = {"auth", "product", "order", "cart"}
)
package com.groceryshop.admin;
```

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   REST API      │    │   Event Bus     │    │   Domain        │
│   Controllers   │◄──►│   (RabbitMQ)    │◄──►│   Services      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   JWT Auth      │    │   Event         │    │   JPA           │
│   Service       │    │   Handlers      │    │   Repositories  │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Domain Model

### Core Entities

#### User Domain
```java
enum UserRole { CUSTOMER, ADMIN }
enum UserStatus { ACTIVE, INACTIVE, SUSPENDED }

@Entity
class User {
    @Id
    private Long id;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### Product Domain
```java
enum ProductCategory { FRUITS, VEGETABLES, DAIRY, MEAT, PANTRY, BEVERAGES }
enum ProductStatus { ACTIVE, INACTIVE, OUT_OF_STOCK }

@Entity
class Product {
    @Id
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private ProductCategory category;
    private ProductStatus status;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### Order Domain
```java
enum OrderStatus { PENDING, CONFIRMED, PROCESSING, SHIPPED, DELIVERED, CANCELLED }

@Entity
class Order {
    @Id
    private Long id;
    @ManyToOne
    private User customer;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}

@Entity
class OrderItem {
    @Id
    private Long id;
    @ManyToOne
    private Order order;
    @ManyToOne
    private Product product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
```

#### Cart Domain
```java
@Entity
class Cart {
    @Id
    private Long id;
    @OneToOne
    private User customer;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "cart")
    private List<CartItem> items;
}

@Entity
class CartItem {
    @Id
    private Long id;
    @ManyToOne
    private Cart cart;
    @ManyToOne
    private Product product;
    private Integer quantity;
    private BigDecimal unitPrice;
    private LocalDateTime addedAt;
}
```

## Event-Driven Architecture

### Event Types

#### Order Events
- `OrderCreatedEvent` - New order placed
- `OrderConfirmedEvent` - Order payment confirmed
- `OrderProcessingEvent` - Order being prepared
- `OrderShippedEvent` - Order dispatched for delivery
- `OrderDeliveredEvent` - Order completed
- `OrderCancelledEvent` - Order cancelled

#### Inventory Events
- `StockUpdatedEvent` - Product stock changed
- `LowStockAlertEvent` - Product stock below threshold
- `ProductAddedEvent` - New product added to catalog
- `ProductUpdatedEvent` - Product information updated
- `ProductRemovedEvent` - Product removed from catalog

#### User Events
- `UserRegisteredEvent` - New user account created
- `UserLoggedInEvent` - User authentication successful
- `UserProfileUpdatedEvent` - User profile modified

#### Cart Events
- `ItemAddedToCartEvent` - Product added to cart
- `ItemRemovedFromCartEvent` - Product removed from cart
- `CartClearedEvent` - Cart emptied
- `CartCheckedOutEvent` - Cart converted to order

### RabbitMQ Configuration

#### Exchanges
- `grocery.orders` - Direct exchange for order events
- `grocery.inventory` - Topic exchange for inventory events
- `grocery.users` - Fanout exchange for user events
- `grocery.notifications` - Headers exchange for notifications

#### Queues
- `order.processing` - Order fulfillment queue
- `inventory.updates` - Stock management queue
- `notification.email` - Email notification queue
- `notification.push` - Push notification queue
- `analytics.events` - Analytics data queue

## Service Layer Architecture

### Domain Services

#### AuthService
```java
interface AuthService {
    AuthenticationResponse login(LoginRequest request);
    User register(RegisterRequest request);
    void logout(String token);
    boolean validateToken(String token);
    User getCurrentUser();
}
```

#### ProductService
```java
interface ProductService {
    List<Product> getAllProducts(ProductSearchCriteria criteria);
    Product getProductById(Long id);
    Product createProduct(CreateProductRequest request);
    Product updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);
    List<Product> getRecommendations(Long userId);
}
```

#### OrderService
```java
interface OrderService {
    Order createOrder(CreateOrderRequest request);
    Order getOrderById(Long id);
    List<Order> getUserOrders(Long userId);
    Order updateOrderStatus(Long id, OrderStatus status);
    void cancelOrder(Long id);
    Order trackOrder(Long id);
}
```

#### CartService
```java
interface CartService {
    Cart getUserCart(Long userId);
    Cart addItemToCart(Long userId, AddToCartRequest request);
    Cart updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);
    Cart removeItemFromCart(Long userId, Long itemId);
    void clearCart(Long userId);
    Order checkout(Long userId, CheckoutRequest request);
}
```

#### InventoryService
```java
interface InventoryService {
    void updateStock(Long productId, Integer quantity);
    Integer getStockLevel(Long productId);
    List<Product> getLowStockProducts();
    void processInventoryUpdate(StockUpdateRequest request);
    InventoryForecast getInventoryForecast(Long productId);
}
```

#### NotificationService
```java
interface NotificationService {
    void sendOrderConfirmation(Order order);
    void sendOrderStatusUpdate(Order order);
    void sendLowStockAlert(Product product);
    void sendWelcomeEmail(User user);
}
```

### Event Handlers

#### OrderEventHandler
- Handles order lifecycle events
- Coordinates with inventory and notification services
- Implements saga pattern for order fulfillment
- Publishes WebSocket events for real-time order tracking

#### InventoryEventHandler
- Processes stock updates
- Triggers low stock alerts
- Updates product availability status
- Publishes WebSocket events for admin inventory alerts

#### UserEventHandler
- Handles user registration events
- Triggers welcome notifications
- Updates user preferences
- Publishes WebSocket events for user notifications

#### SseEventHandler
- Bridges RabbitMQ events to Server-Sent Events streams
- Manages SSE connections for real-time updates
- Filters events based on user permissions and subscriptions
- Handles SSE connection lifecycle and event streaming

### Server-Sent Events (SSE) Integration

#### Event Bridging Architecture
```
RabbitMQ Event → Event Handler → SSE Service → Client EventSource
```

#### SSE Event Format
```json
event: order-update
data: {
  "type": "ORDER_STATUS_UPDATE",
  "orderId": "123",
  "status": "SHIPPED",
  "timestamp": "2024-01-15T10:30:00Z",
  "data": {
    "estimatedDelivery": "2024-01-16T14:00:00Z",
    "trackingNumber": "GS123456789"
  }
}
```

#### Connection Management
- JWT-based authentication for SSE endpoints
- Event filtering based on user permissions and subscriptions
- Automatic client reconnection handled by browser EventSource API
- Lightweight server-side connection management

## REST API Design

### Authentication Endpoints
```
POST   /api/auth/login
POST   /api/auth/register
POST   /api/auth/refresh
POST   /api/auth/logout
GET    /api/auth/me
```

### Product Endpoints
```
GET    /api/products
GET    /api/products/{id}
POST   /api/products          # Admin only
PUT    /api/products/{id}     # Admin only
DELETE /api/products/{id}     # Admin only
GET    /api/products/search
GET    /api/products/recommendations
```

### Cart Endpoints
```
GET    /api/cart
POST   /api/cart/items
PUT    /api/cart/items/{id}
DELETE /api/cart/items/{id}
DELETE /api/cart
POST   /api/cart/checkout
```

### Order Endpoints
```
GET    /api/orders
GET    /api/orders/{id}
POST   /api/orders
PUT    /api/orders/{id}/status    # Admin only
DELETE /api/orders/{id}           # Admin only
GET    /api/orders/{id}/track
```

### Admin Endpoints
```
GET    /api/admin/dashboard
GET    /api/admin/inventory
PUT    /api/admin/inventory/{id}
GET    /api/admin/orders
GET    /api/admin/analytics
```

### Server-Sent Events (SSE) Endpoints
```
SSE streams for real-time updates:
- GET /api/orders/{orderId}/events     # Order status tracking
- GET /api/inventory/events            # Low stock alerts (admin)
- GET /api/notifications/events        # User notifications
```

## Database Schema

### Flyway Migrations
- `V1__create_users_table.sql`
- `V2__create_products_table.sql`
- `V3__create_orders_and_items_tables.sql`
- `V4__create_cart_and_items_tables.sql`
- `V5__create_inventory_tracking.sql`
- `V6__add_indexes_and_constraints.sql`

## Security Implementation

### JWT Configuration
- Access token: 15 minutes
- Refresh token: 7 days
- Password hashing: BCrypt
- CORS configuration for frontend

### Authorization
- Role-based access control
- Method-level security
- API key authentication for admin endpoints

## Configuration Management

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:h2:mem:grocerydb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=validate

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# JWT
app.jwt.secret=mySecretKey
app.jwt.expiration=900000

# WebSocket
app.websocket.enabled=true
app.websocket.allowed-origins=http://localhost:3000,https://grocery-shop.com

# CORS
app.cors.allowed-origins=http://localhost:3000,https://grocery-shop.com
app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
app.cors.allowed-headers=*

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

## Testing Strategy

### Modularity Verification Tests
```java
// ModularityTests.java - Spring Modulith verification
class ModularityTests {

    @Test
    void verifyModularity() {
        ApplicationModules.of(GroceryShopApplication.class).verify();
    }

    @Test
    void verifyAllowedDependencies() {
        var modules = ApplicationModules.of(GroceryShopApplication.class);
        var violations = modules.detectViolations();

        assertThat(violations).isEmpty();
    }
}
```

### Module Integration Tests
```java
// AuthModuleIntegrationTests.java
@ApplicationModuleTest(mode = ApplicationModuleTest.BootstrapMode.DIRECT_DEPENDENCIES)
class AuthModuleIntegrationTests {

    @Autowired
    AuthService authService;

    @Test
    void userRegistrationWorks() {
        // Test auth module in isolation
    }
}

// OrderModuleIntegrationTests.java
@ApplicationModuleTest
class OrderModuleIntegrationTests {

    @Autowired
    OrderService orderService;

    @Test
    void orderCreationPublishesEvents() {
        // Test order module with dependencies
    }
}
```

### Unit Tests
- Service layer testing with Mockito
- Repository testing with @DataJpaTest
- Event handler testing
- Domain logic testing

### Integration Tests
- Full application context testing
- RabbitMQ integration tests
- Database integration tests
- Cross-module integration testing

### Test Containers
- RabbitMQ container for integration tests
- PostgreSQL container for database tests

## Deployment & DevOps

### Docker Configuration
```dockerfile
FROM openjdk:24-jdk-alpine
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - rabbitmq
      - postgres
    environment:
      - SPRING_PROFILES_ACTIVE=docker

  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"
      - "15672:15672"

  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: grocery
      POSTGRES_USER: grocery
      POSTGRES_PASSWORD: password
```

## Monitoring & Observability

### Actuator Endpoints
- Health checks
- Metrics collection
- Log aggregation

### Logging
- Structured logging with Logback
- Correlation IDs for request tracing
- Event logging for audit trails

## Implementation Phases

### Phase 1: Foundation (Week 1)
- [ ] Project setup with Maven
- [ ] Database configuration and Flyway
- [ ] Core entity models
- [ ] Basic repository layer
- [ ] RabbitMQ configuration

### Phase 2: Authentication & Products (Week 2)
- [ ] JWT authentication service
- [ ] User management
- [ ] Product catalog service
- [ ] Search and filtering
- [ ] Basic REST APIs

### Phase 3: Cart & Orders (Week 3)
- [ ] Shopping cart functionality
- [ ] Order processing with events
- [ ] Saga pattern implementation
- [ ] Event handlers
- [ ] Notification system

### Phase 4: Advanced Features (Week 4)
- [ ] Inventory management
- [ ] Product recommendations
- [ ] Admin dashboard APIs
- [ ] Analytics and reporting
- [ ] Comprehensive testing
- [ ] Documentation

## Success Metrics

### Performance
- API response time < 500ms
- Order processing < 2 seconds
- Concurrent users: 1000+
- Message throughput: 1000 msg/sec

### Reliability
- 99.9% uptime
- Event delivery guarantee
- Data consistency across services
- Graceful error handling

### Maintainability
- Code coverage > 80%
- Modular architecture
- Comprehensive documentation
- Easy deployment process

## Risk Mitigation

### Technical Risks
- **Eventual Consistency**: Implement saga pattern and compensation actions
- **Message Loss**: Use publisher confirms and consumer acknowledgments
- **Database Performance**: Implement proper indexing and query optimization
- **Scalability**: Design for horizontal scaling with message partitioning

### Operational Risks
- **RabbitMQ Failures**: Implement circuit breakers and retry mechanisms
- **Database Issues**: Connection pooling and failover strategies
- **Memory Leaks**: Proper resource management and monitoring

This plan provides a comprehensive blueprint for building a robust, event-driven grocery shop backend that can scale and maintain high reliability while following modern architectural patterns.
