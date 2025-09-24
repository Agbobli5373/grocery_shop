package com.groceryshop.order;

import java.util.List;

/**
 * Service interface for order management operations.
 */
public interface OrderService {

    /**
     * Creates a new order from a cart.
     */
    Order createOrder(Long customerId, String deliveryAddress);

    /**
     * Retrieves an order by ID.
     */
    Order getOrderById(Long id);

    /**
     * Retrieves all orders for a customer.
     */
    List<Order> getOrdersByCustomerId(Long customerId);

    /**
     * Updates the status of an order.
     */
    Order updateOrderStatus(Long id, OrderStatus status);

    /**
     * Cancels an order.
     */
    void cancelOrder(Long id);

    /**
     * Retrieves order tracking information.
     */
    Order trackOrder(Long id);
}
