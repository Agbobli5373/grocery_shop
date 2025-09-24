package com.groceryshop.product;

import java.util.List;

/**
 * Service interface for inventory management operations.
 */
public interface InventoryService {

    /**
     * Updates the stock quantity for a product.
     *
     * @param productId the product ID
     * @param quantity the quantity to add/subtract (positive for increase, negative for decrease)
     */
    void updateStock(Long productId, Integer quantity);

    /**
     * Gets the current stock level for a product.
     *
     * @param productId the product ID
     * @return the current stock quantity
     */
    Integer getStockLevel(Long productId);

    /**
     * Gets all products with low stock (below threshold).
     *
     * @return list of products with low stock
     */
    List<Product> getLowStockProducts();

    /**
     * Gets inventory forecast for a product based on historical data.
     *
     * @param productId the product ID
     * @return inventory forecast information
     */
    InventoryForecast getInventoryForecast(Long productId);

    /**
     * Checks if a product has sufficient stock for the requested quantity.
     *
     * @param productId the product ID
     * @param requestedQuantity the quantity requested
     * @return true if sufficient stock is available
     */
    boolean hasSufficientStock(Long productId, Integer requestedQuantity);
}
