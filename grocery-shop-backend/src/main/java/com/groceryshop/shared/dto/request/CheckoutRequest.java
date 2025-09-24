package com.groceryshop.shared.dto.request;

/**
 * Request DTO for cart checkout.
 */
public record CheckoutRequest(
    String deliveryAddress
) {
    public CheckoutRequest {
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("Delivery address is required");
        }
    }
}
