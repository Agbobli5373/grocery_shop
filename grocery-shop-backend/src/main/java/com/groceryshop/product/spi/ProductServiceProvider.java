package com.groceryshop.product.spi;

import com.groceryshop.product.Product;

import java.util.List;
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
     * Gets all products with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of products
     */
    List<Product> findAllProducts(int page, int size);

    /**
     * Counts total products.
     *
     * @return total number of products
     */
    long countProducts();

    /**
     * Counts products by status and stock quantity.
     *
     * @param status the product status
     * @param minStock the minimum stock quantity
     * @return count of products matching criteria
     */
    long countProductsByStatusAndStockGreaterThan(com.groceryshop.product.ProductStatus status, int minStock);

    /**
     * Counts products with stock less than threshold.
     *
     * @param stockThreshold the stock threshold
     * @return count of products below threshold
     */
    long countProductsByStockLessThan(int stockThreshold);

    /**
     * Counts products with exact stock quantity.
     *
     * @param stockQuantity the stock quantity
     * @return count of products with exact stock
     */
    long countProductsByStockQuantity(int stockQuantity);

    /**
     * Finds products with stock less than threshold.
     *
     * @param stockThreshold the stock threshold
     * @return list of products below threshold
     */
    List<Product> findProductsByStockLessThan(int stockThreshold);

    /**
     * Updates product stock quantity.
     *
     * @param productId the product ID
     * @param newStockQuantity the new stock quantity
     */
    void updateProductStock(Long productId, Integer newStockQuantity);
}
