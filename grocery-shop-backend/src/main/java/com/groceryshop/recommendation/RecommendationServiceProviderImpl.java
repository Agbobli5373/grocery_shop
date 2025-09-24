package com.groceryshop.recommendation;

import com.groceryshop.cart.CartItem;
import com.groceryshop.cart.spi.CartServiceProvider;
import com.groceryshop.order.Order;
import com.groceryshop.order.OrderItem;
import com.groceryshop.order.spi.OrderServiceProvider;
import com.groceryshop.product.Product;
import com.groceryshop.product.ProductCategory;
import com.groceryshop.product.ProductStatus;
import com.groceryshop.product.spi.ProductServiceProvider;
import com.groceryshop.recommendation.spi.RecommendationServiceProvider;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of RecommendationServiceProvider using existing SPI providers.
 * This follows the SPI pattern to avoid tight coupling between modules.
 */
@Service
public class RecommendationServiceProviderImpl implements RecommendationServiceProvider {

    private final ProductServiceProvider productServiceProvider;
    private final OrderServiceProvider orderServiceProvider;
    private final CartServiceProvider cartServiceProvider;

    public RecommendationServiceProviderImpl(
            ProductServiceProvider productServiceProvider,
            OrderServiceProvider orderServiceProvider,
            CartServiceProvider cartServiceProvider) {
        this.productServiceProvider = productServiceProvider;
        this.orderServiceProvider = orderServiceProvider;
        this.cartServiceProvider = cartServiceProvider;
    }

    @Override
    public List<Product> findAllProducts(int page, int size) {
        return productServiceProvider.findAllProducts(page, size);
    }

    @Override
    public List<Product> findProductsByCategoryAndStatus(ProductCategory category, ProductStatus status) {
        // This method doesn't exist in ProductServiceProvider, so we need to get all products and filter
        // In a real implementation, we might need to extend the ProductServiceProvider
        List<Product> allProducts = productServiceProvider.findAllProducts(0, Integer.MAX_VALUE);
        return allProducts.stream()
                .filter(product -> product.getCategory() == category && product.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findProductsByStatus(ProductStatus status) {
        // This method doesn't exist in ProductServiceProvider, so we need to get all products and filter
        List<Product> allProducts = productServiceProvider.findAllProducts(0, Integer.MAX_VALUE);
        return allProducts.stream()
                .filter(product -> product.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getUserPurchasedProductIds(Long userId) {
        // We need to get orders for this user and extract product IDs
        // Since OrderServiceProvider doesn't have this method, we need to work with what we have
        // This is a limitation - we might need to extend the SPI interfaces

        // For now, we'll return empty list as we can't access order items through SPI
        // In a real implementation, we'd need to extend OrderServiceProvider
        return Collections.emptyList();
    }

    @Override
    public Map<ProductCategory, Long> getUserCategoryPreferences(Long userId) {
        // Similar issue - we can't access order items through SPI
        return Collections.emptyMap();
    }

    @Override
    public List<Long> getUserCartProductIds(Long userId) {
        Optional<com.groceryshop.cart.Cart> cartOpt = cartServiceProvider.findCartByUserId(userId);
        if (cartOpt.isEmpty()) {
            return Collections.emptyList();
        }

        List<CartItem> cartItems = cartServiceProvider.findCartItemsByCartId(cartOpt.get().getId());
        return cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getId())
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategory> getUserCartCategories(Long userId) {
        Optional<com.groceryshop.cart.Cart> cartOpt = cartServiceProvider.findCartByUserId(userId);
        if (cartOpt.isEmpty()) {
            return Collections.emptyList();
        }

        List<CartItem> cartItems = cartServiceProvider.findCartItemsByCartId(cartOpt.get().getId());
        return cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getCategory())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<Long, Long> getFrequentlyBoughtTogetherProductCounts(Long productId) {
        // This is complex and would require extending the SPI interfaces significantly
        // For now, return empty map
        return Collections.emptyMap();
    }

    @Override
    public Map<Long, Long> getPopularProductCounts() {
        // This would require extending ProductServiceProvider or OrderServiceProvider
        // For now, return empty map
        return Collections.emptyMap();
    }
}
