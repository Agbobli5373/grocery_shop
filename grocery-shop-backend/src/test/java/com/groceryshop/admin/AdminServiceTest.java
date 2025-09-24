package com.groceryshop.admin;

import com.groceryshop.TestDataFactory;
import com.groceryshop.auth.User;
import com.groceryshop.auth.UserRepository;
import com.groceryshop.auth.UserRole;
import com.groceryshop.auth.spi.AuthServiceProvider;
import com.groceryshop.order.Order;
import com.groceryshop.order.OrderStatus;
import com.groceryshop.order.spi.OrderServiceProvider;
import com.groceryshop.product.Product;
import com.groceryshop.product.ProductStatus;
import com.groceryshop.product.spi.ProductServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AdminServiceImpl.
 * Tests dashboard metrics, analytics, inventory status, and administrative operations.
 */
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private OrderServiceProvider orderServiceProvider;

    @Mock
    private ProductServiceProvider productServiceProvider;

    @Mock
    private AuthServiceProvider authServiceProvider;

    @Mock
    private UserRepository userRepository;

    private AdminService adminService;
    private User testUser;
    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(
            orderServiceProvider,
            productServiceProvider,
            authServiceProvider,
            userRepository
        );

        testUser = TestDataFactory.createTestUser();
        testOrder = TestDataFactory.createTestOrder();
        testOrder.setOrderDate(LocalDateTime.now());
        testProduct = TestDataFactory.createTestProduct();
    }

    @Test
    void getDashboardMetrics_ShouldReturnCorrectMetrics() {
        // Given
        when(orderServiceProvider.countOrders()).thenReturn(100L);
        when(userRepository.countByRole(UserRole.CUSTOMER)).thenReturn(50L);
        when(productServiceProvider.countProducts()).thenReturn(200L);
        when(orderServiceProvider.sumTotalAmountByStatus(OrderStatus.DELIVERED))
            .thenReturn(Optional.of(BigDecimal.valueOf(5000.00)));
        when(orderServiceProvider.countOrdersByStatus(OrderStatus.PENDING)).thenReturn(10L);
        when(productServiceProvider.countProductsByStockLessThan(10)).thenReturn(5L);

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        when(orderServiceProvider.countOrdersByDateRange(startOfDay, endOfDay)).thenReturn(3L);

        // When
        AdminService.DashboardMetrics metrics = adminService.getDashboardMetrics();

        // Then
        assertEquals(100L, metrics.totalOrders());
        assertEquals(50L, metrics.totalCustomers());
        assertEquals(200L, metrics.totalProducts());
        assertEquals(BigDecimal.valueOf(5000.00), metrics.totalRevenue());
        assertEquals(10L, metrics.pendingOrders());
        assertEquals(5L, metrics.lowStockProducts());
        assertEquals(50.0, metrics.averageOrderValue(), 0.01); // 5000 / 100
        assertEquals(3L, metrics.ordersToday());
    }

    @Test
    void getDashboardMetrics_ShouldHandleZeroOrders() {
        // Given
        when(orderServiceProvider.countOrders()).thenReturn(0L);
        when(userRepository.countByRole(UserRole.CUSTOMER)).thenReturn(10L);
        when(productServiceProvider.countProducts()).thenReturn(50L);
        when(orderServiceProvider.sumTotalAmountByStatus(OrderStatus.DELIVERED))
            .thenReturn(Optional.empty());
        when(orderServiceProvider.countOrdersByStatus(OrderStatus.PENDING)).thenReturn(0L);
        when(productServiceProvider.countProductsByStockLessThan(10)).thenReturn(2L);

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        when(orderServiceProvider.countOrdersByDateRange(startOfDay, endOfDay)).thenReturn(0L);

        // When
        AdminService.DashboardMetrics metrics = adminService.getDashboardMetrics();

        // Then
        assertEquals(0L, metrics.totalOrders());
        assertEquals(10L, metrics.totalCustomers());
        assertEquals(50L, metrics.totalProducts());
        assertEquals(BigDecimal.ZERO, metrics.totalRevenue());
        assertEquals(0.0, metrics.averageOrderValue(), 0.01);
    }

    @Test
    void getSalesAnalytics_ShouldReturnCorrectAnalytics() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();

        List<Order> ordersInRange = List.of(testOrder);
        when(orderServiceProvider.findOrdersByDateRangeAndStatus(
            any(LocalDateTime.class), any(LocalDateTime.class), eq(OrderStatus.DELIVERED)))
            .thenReturn(ordersInRange);

        when(orderServiceProvider.countOrdersByStatusAndDateRange(
            any(OrderStatus.class), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(5L); // Mock for each status

        // When
        AdminService.SalesAnalytics analytics = adminService.getSalesAnalytics(startDate, endDate);

        // Then
        assertEquals(BigDecimal.valueOf(199.98), analytics.totalRevenue());
        assertEquals(1L, analytics.totalOrders());
        assertTrue(analytics.revenueByCategory().containsKey("All Categories"));
        assertTrue(analytics.ordersByStatus().containsKey("DELIVERED"));
        assertFalse(analytics.dailySales().isEmpty());
    }

    @Test
    void getInventoryStatus_ShouldReturnCorrectStatus() {
        // Given
        when(productServiceProvider.countProducts()).thenReturn(100L);
        when(productServiceProvider.countProductsByStatusAndStockGreaterThan(ProductStatus.ACTIVE, 0))
            .thenReturn(80L);
        when(productServiceProvider.countProductsByStockQuantity(0)).thenReturn(10L);
        when(productServiceProvider.countProductsByStockLessThan(10)).thenReturn(15L);
        when(productServiceProvider.findProductsByStockLessThan(10))
            .thenReturn(List.of(testProduct));

        // When
        AdminService.InventoryStatus status = adminService.getInventoryStatus();

        // Then
        assertEquals(100L, status.totalProducts());
        assertEquals(80L, status.inStockProducts());
        assertEquals(10L, status.outOfStockProducts());
        assertEquals(15L, status.lowStockProducts());
        assertEquals(1, status.lowStockItems().size());
        assertEquals(testProduct, status.lowStockItems().get(0));
    }

    @Test
    void getRecentOrders_ShouldReturnOrdersFromProvider() {
        // Given
        List<Order> expectedOrders = List.of(testOrder);
        when(orderServiceProvider.findAllOrders(0, 10)).thenReturn(expectedOrders);

        // When
        List<Order> recentOrders = adminService.getRecentOrders(10);

        // Then
        assertEquals(expectedOrders, recentOrders);
        verify(orderServiceProvider).findAllOrders(0, 10);
    }

    @Test
    void getTopSellingProducts_ShouldReturnProductSalesData() {
        // Given
        testProduct.setStockQuantity(90); // To get non-zero sold
        List<Product> products = List.of(testProduct);
        when(productServiceProvider.findAllProducts(0, 5)).thenReturn(products);

        // When
        List<AdminService.ProductSalesData> topProducts = adminService.getTopSellingProducts(5);

        // Then
        assertEquals(1, topProducts.size());
        AdminService.ProductSalesData data = topProducts.get(0);
        assertEquals(testProduct, data.product());
        assertEquals(10L, data.totalSold()); // Mock calculation: 100 - 90 = 10
        assertEquals(testProduct.getPrice().multiply(BigDecimal.valueOf(10)), data.totalRevenue());
    }

    @Test
    void getCustomerAnalytics_ShouldReturnCorrectAnalytics() {
        // Given
        when(userRepository.countByRole(UserRole.CUSTOMER)).thenReturn(100L);

        List<User> activeCustomers = List.of(testUser);
        when(orderServiceProvider.findDistinctCustomersWithOrdersAfter(any(LocalDateTime.class)))
            .thenReturn(activeCustomers);

        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        when(userRepository.countByCreatedAtAfterAndRole(startOfMonth, UserRole.CUSTOMER))
            .thenReturn(15L);

        when(orderServiceProvider.countOrders()).thenReturn(250L);

        // Mock top customers data - each element is Object[]{User, Long, BigDecimal}
        List<Object[]> customerDataList = new ArrayList<>();
        customerDataList.add(new Object[]{testUser, 5L, BigDecimal.valueOf(500.00)});
        when(orderServiceProvider.findTopCustomersByOrderCount(5))
            .thenReturn(customerDataList);

        // When
        AdminService.CustomerAnalytics analytics = adminService.getCustomerAnalytics();

        // Then
        assertEquals(100L, analytics.totalCustomers());
        assertEquals(1L, analytics.activeCustomers());
        assertEquals(15L, analytics.newCustomersThisMonth());
        assertEquals(2.5, analytics.averageOrdersPerCustomer(), 0.01); // 250 / 100
        assertEquals(1, analytics.topCustomers().size());

        AdminService.TopCustomerData topCustomer = analytics.topCustomers().get(0);
        assertEquals("Test User", topCustomer.customerName());
        assertEquals(testUser.getEmail(), topCustomer.customerEmail());
        assertEquals(5L, topCustomer.orderCount());
        assertEquals(BigDecimal.valueOf(500.00), topCustomer.totalSpent());
    }

    @Test
    void updateProductStock_ShouldDelegateToProductServiceProvider() {
        // When
        adminService.updateProductStock(1L, 50);

        // Then
        verify(productServiceProvider).updateProductStock(1L, 50);
    }

    @Test
    void getSystemHealth_ShouldReturnHealthMetrics() {
        // When
        AdminService.SystemHealth health = adminService.getSystemHealth();

        // Then
        assertEquals("UP", health.status());
        assertTrue(health.services().containsKey("database"));
        assertTrue(health.services().containsKey("rabbitmq"));
        assertTrue(health.services().containsKey("cache"));
        assertTrue(health.uptimeSeconds() > 0);
        assertTrue(health.memoryUsagePercent() >= 0.0);
        assertTrue(health.cpuUsagePercent() >= 0.0);
    }
}
