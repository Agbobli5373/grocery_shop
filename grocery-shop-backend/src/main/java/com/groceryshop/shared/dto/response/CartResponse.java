package com.groceryshop.shared.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for cart information.
 */
public record CartResponse(
    Long id,
    Long customerId,
    BigDecimal totalAmount,
    List<CartItemResponse> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal totalPrice,
        LocalDateTime addedAt
    ) {}
}
