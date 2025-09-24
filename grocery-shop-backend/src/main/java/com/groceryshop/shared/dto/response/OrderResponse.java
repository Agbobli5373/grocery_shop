package com.groceryshop.shared.dto.response;

import com.groceryshop.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for order information.
 */
public record OrderResponse(
    Long id,
    Long customerId,
    String customerEmail,
    BigDecimal totalAmount,
    OrderStatus status,
    String deliveryAddress,
    LocalDateTime orderDate,
    LocalDateTime deliveryDate,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<OrderItemResponse> items
) {
    public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal totalPrice
    ) {}
}
