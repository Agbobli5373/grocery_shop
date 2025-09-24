package com.groceryshop.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for admin operations and dashboard functionality.
 * Provides endpoints for administrative tasks, analytics, and system monitoring.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative operations and dashboard")
public class AdminController {

    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Gets dashboard metrics overview.
     *
     * @return dashboard metrics
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard metrics", description = "Retrieves key performance indicators for the admin dashboard")
    public ResponseEntity<AdminService.DashboardMetrics> getDashboardMetrics() {
        log.info("Fetching dashboard metrics");
        AdminService.DashboardMetrics metrics = adminService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Gets sales analytics for a date range.
     *
     * @param startDate start date for analytics
     * @param endDate end date for analytics
     * @return sales analytics data
     */
    @GetMapping("/analytics/sales")
    @Operation(summary = "Get sales analytics", description = "Retrieves sales analytics for the specified date range")
    public ResponseEntity<AdminService.SalesAnalytics> getSalesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Fetching sales analytics from {} to {}", startDate, endDate);
        AdminService.SalesAnalytics analytics = adminService.getSalesAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    /**
     * Gets inventory status overview.
     *
     * @return inventory status information
     */
    @GetMapping("/inventory/status")
    @Operation(summary = "Get inventory status", description = "Retrieves current inventory status and low stock alerts")
    public ResponseEntity<AdminService.InventoryStatus> getInventoryStatus() {
        log.info("Fetching inventory status");
        AdminService.InventoryStatus status = adminService.getInventoryStatus();
        return ResponseEntity.ok(status);
    }

    /**
     * Gets recent orders for admin review.
     *
     * @param limit maximum number of orders to return (default: 10)
     * @return list of recent orders
     */
    @GetMapping("/orders/recent")
    @Operation(summary = "Get recent orders", description = "Retrieves the most recent orders for admin review")
    public ResponseEntity<List<com.groceryshop.order.Order>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching {} recent orders", limit);
        List<com.groceryshop.order.Order> orders = adminService.getRecentOrders(limit);
        return ResponseEntity.ok(orders);
    }

    /**
     * Gets top-selling products.
     *
     * @param limit maximum number of products to return (default: 10)
     * @return list of top-selling products with sales data
     */
    @GetMapping("/products/top-selling")
    @Operation(summary = "Get top-selling products", description = "Retrieves the best-selling products with sales metrics")
    public ResponseEntity<List<AdminService.ProductSalesData>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Fetching top {} selling products", limit);
        List<AdminService.ProductSalesData> products = adminService.getTopSellingProducts(limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Gets customer analytics.
     *
     * @return customer analytics data
     */
    @GetMapping("/analytics/customers")
    @Operation(summary = "Get customer analytics", description = "Retrieves customer behavior and demographic analytics")
    public ResponseEntity<AdminService.CustomerAnalytics> getCustomerAnalytics() {
        log.info("Fetching customer analytics");
        AdminService.CustomerAnalytics analytics = adminService.getCustomerAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Updates product stock levels.
     *
     * @param productId the product ID
     * @param newStockQuantity the new stock quantity
     * @return success response
     */
    @PutMapping("/inventory/stock/{productId}")
    @Operation(summary = "Update product stock", description = "Updates the stock quantity for a specific product")
    public ResponseEntity<Void> updateProductStock(
            @PathVariable Long productId,
            @RequestParam Integer newStockQuantity) {
        log.info("Updating stock for product {} to {}", productId, newStockQuantity);
        adminService.updateProductStock(productId, newStockQuantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Gets system health metrics.
     *
     * @return system health information
     */
    @GetMapping("/system/health")
    @Operation(summary = "Get system health", description = "Retrieves system health and performance metrics")
    public ResponseEntity<AdminService.SystemHealth> getSystemHealth() {
        log.info("Fetching system health metrics");
        AdminService.SystemHealth health = adminService.getSystemHealth();
        return ResponseEntity.ok(health);
    }
}
