package com.groceryshop.product;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents inventory forecast information for a product.
 */
public record InventoryForecast(
    Long productId,
    String productName,
    Integer currentStock,
    Integer averageDailySales,
    Integer forecastedDemand,
    LocalDate nextRestockDate,
    Integer recommendedRestockQuantity,
    BigDecimal confidenceLevel
) {

    /**
     * Calculates the days until stock runs out based on current sales rate.
     *
     * @return days until stock depletion, or null if no sales data
     */
    public Integer getDaysUntilStockOut() {
        if (averageDailySales == null || averageDailySales == 0) {
            return null;
        }
        return currentStock / averageDailySales;
    }

    /**
     * Checks if the product needs urgent restocking.
     *
     * @return true if stock is critically low
     */
    public boolean needsUrgentRestock() {
        Integer daysUntilOut = getDaysUntilStockOut();
        return daysUntilOut != null && daysUntilOut <= 7; // Less than a week
    }
}
