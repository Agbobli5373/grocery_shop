package com.groceryshop.cart;

/**
 * Event published when an item is added to a cart.
 */
public record ItemAddedToCartEvent(
    Object source,
    Long cartId,
    Long userId,
    Long productId,
    String productName,
    Integer quantity,
    Integer newStockLevel
) {}
