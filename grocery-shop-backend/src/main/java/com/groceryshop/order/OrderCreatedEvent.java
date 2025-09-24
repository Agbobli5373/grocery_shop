package com.groceryshop.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Event published when a new order is created.
 */
public record OrderCreatedEvent(
    Long orderId,
    Long customerId,
    BigDecimal totalAmount,
    String deliveryAddress,
    LocalDateTime orderDate,
    String customerEmail
) {

    public OrderCreatedEvent(Long orderId, Long customerId, BigDecimal totalAmount) {
        this(orderId, customerId, totalAmount, null, LocalDateTime.now(), null);
    }
}
