package com.groceryshop.recommendation.spi;

import com.groceryshop.product.Product;
import com.groceryshop.product.ProductCategory;
import com.groceryshop.product.ProductStatus;

import java.util.List;
import java.util.Map;

/**
 * Service Provider Interface for recommendation services.
 * This interface defines the contract that the recommendation module needs from other modules.
 */
public interface RecommendationServiceProvider {

    /**
     * Gets all products with pagination.
     */
    List<Product> findAllProducts(int page, int size);

    /**
     * Finds products by category and status.
     */
    List<Product> findProductsByCategoryAndStatus(ProductCategory category, ProductStatus status);

    /**
     * Finds products by status.
     */
    List<Product> findProductsByStatus(ProductStatus status);

    /**
     * Gets user purchase history (product IDs).
     */
    List<Long> getUserPurchasedProductIds(Long userId);

    /**
     * Gets user category purchase preferences.
     */
    Map<ProductCategory, Long> getUserCategoryPreferences(Long userId);

    /**
     * Gets current cart product IDs for a user.
     */
    List<Long> getUserCartProductIds(Long userId);

    /**
     * Gets current cart categories for a user.
     */
    List<ProductCategory> getUserCartCategories(Long userId);

    /**
     * Gets frequently bought together products for a given product.
     */
    Map<Long, Long> getFrequentlyBoughtTogetherProductCounts(Long productId);

    /**
     * Gets popular products based on purchase counts.
     */
    Map<Long, Long> getPopularProductCounts();
}
