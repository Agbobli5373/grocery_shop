package com.groceryshop.admin;

import com.groceryshop.order.Order;
import com.groceryshop.product.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for admin operations and dashboard functionality.
 */
public interface AdminService {

    /**
     * Gets dashboard metrics for the admin overview.
     *
     * @return dashboard metrics
     */
    DashboardMetrics getDashboardMetrics();

    /**
     * Gets sales analytics for a date range.
     *
     * @param startDate start date for analytics
     * @param endDate end date for analytics
     * @return sales analytics data
     */
    SalesAnalytics getSalesAnalytics(LocalDate startDate, LocalDate endDate);

    /**
     * Gets inventory status overview.
     *
     * @return inventory status information
     */
    InventoryStatus getInventoryStatus();

    /**
     * Gets recent orders for admin review.
     *
     * @param limit maximum number of orders to return
     * @return list of recent orders
     */
    List<Order> getRecentOrders(int limit);

    /**
     * Gets top-selling products.
     *
     * @param limit maximum number of products to return
     * @return list of top-selling products with sales data
     */
    List<ProductSalesData> getTopSellingProducts(int limit);

    /**
     * Gets customer analytics.
     *
     * @return customer analytics data
     */
    CustomerAnalytics getCustomerAnalytics();

    /**
     * Updates product stock levels (admin operation).
     *
     * @param productId the product ID
     * @param newStockQuantity the new stock quantity
     */
    void updateProductStock(Long productId, Integer newStockQuantity);

    /**
     * Gets system health metrics.
     *
     * @return system health information
     */
    SystemHealth getSystemHealth();

    /**
     * Records for dashboard metrics.
     */
    record DashboardMetrics(
        long totalOrders,
        long totalCustomers,
        long totalProducts,
        BigDecimal totalRevenue,
        long pendingOrders,
        long lowStockProducts,
        double averageOrderValue,
        long ordersToday
    ) {}

    record SalesAnalytics(
        BigDecimal totalRevenue,
        long totalOrders,
        Map<String, BigDecimal> revenueByCategory,
        Map<String, Long> ordersByStatus,
        List<DailySalesData> dailySales
    ) {}

    record DailySalesData(
        LocalDate date,
        BigDecimal revenue,
        long orderCount
    ) {}

    record InventoryStatus(
        long totalProducts,
        long inStockProducts,
        long outOfStockProducts,
        long lowStockProducts,
        List<Product> lowStockItems
    ) {}

    record ProductSalesData(
        Product product,
        long totalSold,
        BigDecimal totalRevenue
    ) {}

    record CustomerAnalytics(
        long totalCustomers,
        long activeCustomers,
        long newCustomersThisMonth,
        double averageOrdersPerCustomer,
        List<TopCustomerData> topCustomers
    ) {}

    record TopCustomerData(
        String customerName,
        String customerEmail,
        long orderCount,
        BigDecimal totalSpent
    ) {}

    record SystemHealth(
        String status,
        Map<String, String> services,
        long uptimeSeconds,
        double memoryUsagePercent,
        double cpuUsagePercent
    ) {}
}
