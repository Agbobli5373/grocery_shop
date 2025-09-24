package com.groceryshop.product;

import java.time.LocalDateTime;

/**
 * Event published when a product's stock falls below the threshold.
 */
public record LowStockAlertEvent(
    Long productId,
    String productName,
    Integer currentStock,
    Integer threshold,
    LocalDateTime alertTime
) {
}
