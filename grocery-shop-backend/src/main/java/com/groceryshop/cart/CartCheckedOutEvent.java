package com.groceryshop.cart;

import java.math.BigDecimal;

/**
 * Event published when a cart is checked out and converted to an order.
 */
public record CartCheckedOutEvent(
    Object source,
    Long cartId,
    Long userId,
    Long orderId,
    BigDecimal totalAmount,
    Integer itemCount
) {}
