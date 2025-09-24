package com.groceryshop.shared.dto.request;

/**
 * Request DTO for adding items to cart.
 */
public record AddToCartRequest(
    Long productId,
    Integer quantity
) {
    public AddToCartRequest {
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }
    }
}
