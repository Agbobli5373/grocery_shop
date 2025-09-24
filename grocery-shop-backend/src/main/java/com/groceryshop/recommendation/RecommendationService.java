package com.groceryshop.recommendation;

import com.groceryshop.product.Product;
import com.groceryshop.product.ProductCategory;

import java.util.List;

/**
 * Service interface for product recommendations.
 */
public interface RecommendationService {

    /**
     * Get personalized recommendations for a user based on their purchase history.
     */
    List<Product> getPersonalizedRecommendations(Long userId, int limit);

    /**
     * Get recommendations based on current cart contents.
     */
    List<Product> getCartBasedRecommendations(Long userId, int limit);

    /**
     * Get recommendations for a specific product category.
     */
    List<Product> getCategoryRecommendations(ProductCategory category, int limit);

    /**
     * Get popular/trending products across all categories.
     */
    List<Product> getPopularProducts(int limit);

    /**
     * Get products frequently bought together with the given product.
     */
    List<Product> getFrequentlyBoughtTogether(Long productId, int limit);

    /**
     * Get recommendations based on user's preferred categories.
     */
    List<Product> getUserPreferredCategoriesRecommendations(Long userId, int limit);
}
