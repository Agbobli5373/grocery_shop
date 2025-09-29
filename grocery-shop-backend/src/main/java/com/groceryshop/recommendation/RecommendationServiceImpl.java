package com.groceryshop.recommendation;

import com.groceryshop.product.Product;
import com.groceryshop.product.ProductCategory;
import com.groceryshop.product.ProductStatus;
import com.groceryshop.recommendation.spi.RecommendationServiceProvider;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the RecommendationService with multiple recommendation algorithms.
 * Uses SPI pattern to avoid tight coupling with other modules.
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationServiceProvider recommendationServiceProvider;

    public RecommendationServiceImpl(RecommendationServiceProvider recommendationServiceProvider) {
        this.recommendationServiceProvider = recommendationServiceProvider;
    }

    @Override
    public List<Product> getPersonalizedRecommendations(Long userId, int limit) {
        // Get a user's purchase history
        List<Long> purchasedProductIds = recommendationServiceProvider.getUserPurchasedProductIds(userId);

        if (purchasedProductIds.isEmpty()) {
            // New user - return popular products
            return getPopularProducts(limit);
        }

        // Get user's preferred categories based on purchase history
        Map<ProductCategory, Long> categoryPreferences = recommendationServiceProvider.getUserCategoryPreferences(userId);

        // Find products in preferred categories that the user hasn't purchased
        List<ProductCategory> preferredCategories = categoryPreferences.entrySet()
                .stream()
                .sorted(Map.Entry.<ProductCategory, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();

        List<Product> recommendations = new ArrayList<>();
        for (ProductCategory category : preferredCategories) {
            List<Product> categoryProducts = recommendationServiceProvider
                    .findProductsByCategoryAndStatus(category, ProductStatus.ACTIVE)
                    .stream()
                    .filter(product -> !purchasedProductIds.contains(product.getId()))
                    .filter(Product::isInStock)
                    .toList();

            recommendations.addAll(categoryProducts);
            if (recommendations.size() >= limit) break;
        }

        // If we don't have enough recommendations, add popular products
        if (recommendations.size() < limit) {
            List<Product> popularProducts = getPopularProducts(limit - recommendations.size())
                    .stream()
                    .filter(product -> !purchasedProductIds.contains(product.getId()))
                    .filter(product -> !recommendations.contains(product))
                    .toList();
            recommendations.addAll(popularProducts);
        }

        return recommendations.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<Product> getCartBasedRecommendations(Long userId, int limit) {
        // Get current cart items
        List<Long> cartProductIds = recommendationServiceProvider.getUserCartProductIds(userId);

        if (cartProductIds.isEmpty()) {
            return getPersonalizedRecommendations(userId, limit);
        }

        // Get categories from cart items
        List<ProductCategory> cartCategories = recommendationServiceProvider.getUserCartCategories(userId);

        // Recommend complementary products from different categories
        List<Product> recommendations = new ArrayList<>();
        for (ProductCategory category : ProductCategory.values()) {
            if (!cartCategories.contains(category)) {
                List<Product> categoryProducts = recommendationServiceProvider
                        .findProductsByCategoryAndStatus(category, ProductStatus.ACTIVE)
                        .stream()
                        .filter(Product::isInStock)
                        .limit(limit / 2) // Distribute recommendations across categories
                        .toList();
                recommendations.addAll(categoryProducts);
            }
        }

        return recommendations.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<Product> getCategoryRecommendations(ProductCategory category, int limit) {
        return recommendationServiceProvider
                .findProductsByCategoryAndStatus(category, ProductStatus.ACTIVE)
                .stream()
                .filter(Product::isInStock)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getPopularProducts(int limit) {
        // Get popular products based on purchase counts
        Map<Long, Long> productPurchaseCounts = recommendationServiceProvider.getPopularProductCounts();

        return getProducts(limit, productPurchaseCounts);
    }

    @NotNull
    private List<Product> getProducts(int limit, Map<Long, Long> productPurchaseCounts) {
        return productPurchaseCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .map(entry -> {
                    // Find product by ID from all products
                    return recommendationServiceProvider.findAllProducts(0, Integer.MAX_VALUE)
                            .stream()
                            .filter(product -> product.getId().equals(entry.getKey()))
                            .findFirst()
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .filter(product -> product.getStatus() == ProductStatus.ACTIVE)
                .filter(Product::isInStock)
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getFrequentlyBoughtTogether(Long productId, int limit) {
        // Get frequently bought together products
        Map<Long, Long> coPurchaseCounts = recommendationServiceProvider.getFrequentlyBoughtTogetherProductCounts(productId);

        if (coPurchaseCounts.isEmpty()) {
            return getPopularProducts(limit);
        }

        return getProducts(limit, coPurchaseCounts);
    }

    @Override
    public List<Product> getUserPreferredCategoriesRecommendations(Long userId, int limit) {
        // This is essentially the same as personalized recommendations
        return getPersonalizedRecommendations(userId, limit);
    }
}
