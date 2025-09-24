package com.groceryshop.cart;

/**
 * Event published when an item is removed from a cart.
 */
public record ItemRemovedFromCartEvent(
    Object source,
    Long cartId,
    Long userId,
    Long productId,
    String productName,
    Integer quantityRemoved,
    Integer newStockLevel
) {}
