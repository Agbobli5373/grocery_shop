package com.groceryshop.product.spi;

import com.groceryshop.product.Product;

import java.util.Optional;

/**
 * Service Provider Interface for product services.
 * This interface defines the contract that other modules can use to interact with product functionality.
 */
public interface ProductServiceProvider {

    /**
     * Finds a product by its ID.
     *
     * @param productId the product ID
     * @return Optional containing the product if found
     */
    Optional<Product> findProductById(Long productId);

    /**
     * Updates the stock quantity for a product.
     *
     * @param productId the product ID
     * @param newStockQuantity the new stock quantity
     */
    void updateProductStock(Long productId, Integer newStockQuantity);
}
