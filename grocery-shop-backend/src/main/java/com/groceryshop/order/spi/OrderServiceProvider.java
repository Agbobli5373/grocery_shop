package com.groceryshop.order.spi;

import com.groceryshop.order.Order;
import com.groceryshop.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service Provider Interface for order services.
 * This interface defines the contract that other modules can use to interact with order functionality.
 */
public interface OrderServiceProvider {

    /**
     * Finds an order by its ID.
     *
     * @param orderId the order ID
     * @return Optional containing the order if found
     */
    Optional<Order> findOrderById(Long orderId);

    /**
     * Gets all orders with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of orders
     */
    List<Order> findAllOrders(int page, int size);

    /**
     * Counts total orders.
     *
     * @return total number of orders
     */
    long countOrders();

    /**
     * Counts orders by status.
     *
     * @param status the order status
     * @return count of orders with the specified status
     */
    long countOrdersByStatus(OrderStatus status);

    /**
     * Sums total amount by status.
     *
     * @param status the order status
     * @return sum of total amounts for orders with the specified status
     */
    Optional<BigDecimal> sumTotalAmountByStatus(OrderStatus status);

    /**
     * Counts orders by date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return count of orders in the date range
     */
    long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Counts orders by status and date range.
     *
     * @param status the order status
     * @param startDate the start date
     * @param endDate the end date
     * @return count of orders matching criteria
     */
    long countOrdersByStatusAndDateRange(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds orders by date range and status.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @param status the order status
     * @return list of orders matching criteria
     */
    List<Order> findOrdersByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status);

    /**
     * Finds distinct customers with orders after a certain date.
     *
     * @param since the date threshold
     * @return list of users who have placed orders since the date
     */
    List<com.groceryshop.auth.User> findDistinctCustomersWithOrdersAfter(LocalDateTime since);

    /**
     * Gets top customers by order count.
     *
     * @param limit the maximum number of customers to return
     * @return list of customer data with order counts and total spent
     */
    List<Object[]> findTopCustomersByOrderCount(int limit);
}
