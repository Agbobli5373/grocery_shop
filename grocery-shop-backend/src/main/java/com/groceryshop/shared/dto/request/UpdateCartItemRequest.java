package com.groceryshop.shared.dto.request;

/**
 * Request DTO for updating cart item quantity.
 */
public record UpdateCartItemRequest(
    Integer quantity
) {
    public UpdateCartItemRequest {
        if (quantity == null || quantity <= 0) {
            quantity = 1;
        }
    }
}
