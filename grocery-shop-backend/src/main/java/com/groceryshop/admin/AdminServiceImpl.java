package com.groceryshop.admin;

import com.groceryshop.auth.User;
import com.groceryshop.auth.UserRepository;
import com.groceryshop.order.Order;
import com.groceryshop.order.OrderStatus;
import com.groceryshop.order.spi.OrderServiceProvider;
import com.groceryshop.product.Product;
import com.groceryshop.product.spi.ProductServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the AdminService interface.
 * Provides dashboard metrics, analytics, and administrative operations.
 */
@Service
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminServiceImpl.class);

    private final OrderServiceProvider orderServiceProvider;
    private final ProductServiceProvider productServiceProvider;
    private final UserRepository userRepository; // Keep direct access to user analytics

    public AdminServiceImpl(
            OrderServiceProvider orderServiceProvider,
            ProductServiceProvider productServiceProvider,
            UserRepository userRepository) {
        this.orderServiceProvider = orderServiceProvider;
        this.productServiceProvider = productServiceProvider;
        this.userRepository = userRepository;
    }

    // Backwards-compatible constructor overload used in some tests (accepts an extra AuthServiceProvider)
    public AdminServiceImpl(
            OrderServiceProvider orderServiceProvider,
            ProductServiceProvider productServiceProvider,
            com.groceryshop.auth.spi.AuthServiceProvider authServiceProvider,
            UserRepository userRepository) {
        this(orderServiceProvider, productServiceProvider, userRepository);
        // authServiceProvider is accepted for compatibility; not used in current implementation
    }

    @Override
    public DashboardMetrics getDashboardMetrics() {
        log.debug("Calculating dashboard metrics");

        long totalOrders = orderServiceProvider.countOrders();
        long totalCustomers = userRepository.countByRole(com.groceryshop.auth.UserRole.CUSTOMER);
        long totalProducts = productServiceProvider.countProducts();

        BigDecimal totalRevenue = orderServiceProvider.sumTotalAmountByStatus(OrderStatus.DELIVERED)
            .orElse(BigDecimal.ZERO);

        long pendingOrders = orderServiceProvider.countOrdersByStatus(OrderStatus.PENDING);
        long lowStockProducts = productServiceProvider.countProductsByStockLessThan(10);

        double averageOrderValue = totalOrders > 0 ?
            totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP).doubleValue() : 0.0;

        LocalDateTime startOfDay = AdminAnalyticsUtils.toStartOfDay(LocalDate.now());
        LocalDateTime endOfDay = AdminAnalyticsUtils.toEndOfDay(LocalDate.now());
        long ordersToday = orderServiceProvider.countOrdersByDateRange(startOfDay, endOfDay);

        return new DashboardMetrics(
            totalOrders,
            totalCustomers,
            totalProducts,
            totalRevenue,
            pendingOrders,
            lowStockProducts,
            averageOrderValue,
            ordersToday
        );
    }

    @Override
    public SalesAnalytics getSalesAnalytics(LocalDate startDate, LocalDate endDate) {
        log.debug("Calculating sales analytics from {} to {}", startDate, endDate);

        LocalDateTime startDateTime = AdminAnalyticsUtils.toStartOfDay(startDate);
        LocalDateTime endDateTime = AdminAnalyticsUtils.toEndOfDay(endDate);

        List<Order> ordersInRange = orderServiceProvider.findOrdersByDateRangeAndStatus(
            startDateTime, endDateTime, OrderStatus.DELIVERED);

        BigDecimal totalRevenue = AdminAnalyticsUtils.sumTotalRevenue(ordersInRange);

        long totalOrders = ordersInRange.size();

        // Revenue by category (simplified - would need order items analysis)
        Map<String, BigDecimal> revenueByCategory = new HashMap<>();
        revenueByCategory.put("All Categories", totalRevenue);

        // Orders by status
        Map<String, Long> ordersByStatus = AdminAnalyticsUtils.buildOrdersByStatus(orderServiceProvider, startDateTime, endDateTime);

        // Daily sales data (uses reusable helper)
        List<DailySalesData> dailySales = AdminAnalyticsUtils.buildDailySales(orderServiceProvider, startDate, endDate);

        return new SalesAnalytics(totalRevenue, totalOrders, revenueByCategory, ordersByStatus, dailySales);
    }

    @Override
    public InventoryStatus getInventoryStatus() {
        log.debug("Calculating inventory status");

        long totalProducts = productServiceProvider.countProducts();
        long inStockProducts = productServiceProvider.countProductsByStatusAndStockGreaterThan(
            com.groceryshop.product.ProductStatus.ACTIVE, 0);
        long outOfStockProducts = productServiceProvider.countProductsByStockQuantity(0);
        long lowStockProducts = productServiceProvider.countProductsByStockLessThan(10);

        List<Product> lowStockItems = productServiceProvider.findProductsByStockLessThan(10);

        return new InventoryStatus(
            totalProducts,
            inStockProducts,
            outOfStockProducts,
            lowStockProducts,
            lowStockItems
        );
    }

    @Override
    public List<Order> getRecentOrders(int limit) {
        log.debug("Fetching {} recent orders", limit);
        return orderServiceProvider.findAllOrders(0, limit);
    }

    @Override
    public List<ProductSalesData> getTopSellingProducts(int limit) {
        log.debug("Calculating top {} selling products", limit);

        List<Product> products = productServiceProvider.findAllProducts(0, limit);

        return AdminAnalyticsUtils.mapProductsToSalesData(products);
    }

    @Override
    public CustomerAnalytics getCustomerAnalytics() {
        log.debug("Calculating customer analytics");

        long totalCustomers = userRepository.countByRole(com.groceryshop.auth.UserRole.CUSTOMER);

        // Active customers (customers with orders in the last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        long activeCustomers = orderServiceProvider.findDistinctCustomersWithOrdersAfter(thirtyDaysAgo).size();

        // New customers this month
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        long newCustomersThisMonth = userRepository.countByCreatedAtAfterAndRole(startOfMonth, com.groceryshop.auth.UserRole.CUSTOMER);

        // Average orders per customer
        double averageOrdersPerCustomer = totalCustomers > 0 ?
            (double) orderServiceProvider.countOrders() / totalCustomers : 0.0;

        // Top customers (uses reusable helper)
        List<TopCustomerData> topCustomers = AdminAnalyticsUtils.mapTopCustomers(orderServiceProvider.findTopCustomersByOrderCount(5));

        return new CustomerAnalytics(
            totalCustomers,
            activeCustomers,
            newCustomersThisMonth,
            averageOrdersPerCustomer,
            topCustomers
        );
    }

    @Override
    @Transactional
    public void updateProductStock(Long productId, Integer newStockQuantity) {
        log.info("Updating stock for product {} to {}", productId, newStockQuantity);

        productServiceProvider.updateProductStock(productId, newStockQuantity);

        log.info("Stock updated successfully for product {}", productId);
    }

    @Override
    public SystemHealth getSystemHealth() {
        log.debug("Checking system health");

        // Basic system health check
        String status = "UP";

        Map<String, String> services = new HashMap<>();
        services.put("database", "UP"); // Would check actual DB connectivity
        services.put("rabbitmq", "UP"); // Would check RabbitMQ connectivity
        services.put("cache", "UP"); // Would check Redis/cache connectivity

        // Mock uptime - in real implementation, track application start time
        long uptimeSeconds = 3600; // 1 hour mock

        // Mock system metrics - in real implementation, use JMX or system monitoring
        double memoryUsagePercent = 65.5;
        double cpuUsagePercent = 23.1;

        return new SystemHealth(status, services, uptimeSeconds, memoryUsagePercent, cpuUsagePercent);
    }
}
