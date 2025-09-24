package com.groceryshop.order;

import java.time.LocalDateTime;

/**
 * Event published when an order status is updated.
 */
public record OrderStatusUpdatedEvent(
    Long orderId,
    OrderStatus oldStatus,
    OrderStatus newStatus,
    LocalDateTime updatedAt,
    String updatedBy
) {

    public OrderStatusUpdatedEvent(Long orderId, OrderStatus oldStatus, OrderStatus newStatus) {
        this(orderId, oldStatus, newStatus, LocalDateTime.now(), null);
    }
}
