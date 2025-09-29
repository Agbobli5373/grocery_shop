package com.groceryshop.admin;

import com.groceryshop.order.Order;
import com.groceryshop.order.OrderStatus;
import com.groceryshop.order.spi.OrderServiceProvider;
import com.groceryshop.product.Product;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Reusable admin analytics helper methods.
 */
public final class AdminAnalyticsUtils {

    private AdminAnalyticsUtils() {}

    public static LocalDateTime toStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime toEndOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    public static BigDecimal sumTotalRevenue(List<Order> orders) {
        return orders.stream()
            .map(Order::getTotalAmount)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static Map<String, Long> buildOrdersByStatus(OrderServiceProvider provider, LocalDateTime start, LocalDateTime end) {
        Map<OrderStatus, Long> byStatus = Arrays.stream(OrderStatus.values())
            .collect(Collectors.toMap(
                status -> status,
                status -> provider.countOrdersByStatusAndDateRange(status, start, end)
            ));

        return byStatus.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().name(), Map.Entry::getValue));
    }

    public static List<AdminService.DailySalesData> buildDailySales(OrderServiceProvider provider, LocalDate start, LocalDate end) {
        List<AdminService.DailySalesData> dailySales = new ArrayList<>();
        LocalDate current = start;
        while (!current.isAfter(end)) {
            LocalDateTime dayStart = toStartOfDay(current);
            LocalDateTime dayEnd = toEndOfDay(current);

            List<Order> dayOrders = provider.findOrdersByDateRangeAndStatus(dayStart, dayEnd, OrderStatus.DELIVERED);
            BigDecimal dayRevenue = sumTotalRevenue(dayOrders);

            dailySales.add(new AdminService.DailySalesData(current, dayRevenue, dayOrders.size()));
            current = current.plusDays(1);
        }
        return dailySales;
    }

    public static List<AdminService.TopCustomerData> mapTopCustomers(List<Object[]> results) {
        if (results == null) return List.of();
        return results.stream()
            .map(result -> {
                com.groceryshop.auth.User customer = (com.groceryshop.auth.User) result[0];
                Long orderCount = (Long) result[1];
                BigDecimal totalSpent = (BigDecimal) result[2];
                return new AdminService.TopCustomerData(
                    customer.getFirstName() + " " + customer.getLastName(),
                    customer.getEmail(),
                    orderCount,
                    totalSpent
                );
            })
            .collect(Collectors.toList());
    }

    public static List<AdminService.ProductSalesData> mapProductsToSalesData(List<Product> products) {
        if (products == null) return List.of();
        return products.stream()
            .map(product -> {
                long totalSold = Math.max(0, 100 - product.getStockQuantity());
                BigDecimal totalRevenue = product.getPrice().multiply(BigDecimal.valueOf(totalSold));
                return new AdminService.ProductSalesData(product, totalSold, totalRevenue);
            })
            .collect(Collectors.toList());
    }
}

