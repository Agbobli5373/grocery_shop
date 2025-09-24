package com.groceryshop.recommendation;

import com.groceryshop.TestDataFactory;
import com.groceryshop.product.Product;
import com.groceryshop.product.ProductCategory;
import com.groceryshop.product.ProductStatus;
import com.groceryshop.recommendation.spi.RecommendationServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationServiceProvider recommendationServiceProvider;

    private RecommendationService recommendationService;
    private Product testProduct1;
    private Product testProduct2;

    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationServiceImpl(recommendationServiceProvider);

        testProduct1 = TestDataFactory.createTestProduct();
        testProduct1.setId(1L);
        testProduct1.setStatus(ProductStatus.ACTIVE);
        testProduct1.setStockQuantity(50);

        testProduct2 = TestDataFactory.createTestProduct();
        testProduct2.setId(2L);
        testProduct2.setStatus(ProductStatus.ACTIVE);
        testProduct2.setStockQuantity(20);
    }

    @Test
    void getPersonalizedRecommendations_WithPurchaseHistory_ShouldReturnFilteredRecommendations() {
        // Given
        Long userId = 1L;
        int limit = 3;
        List<Long> purchasedIds = List.of(1L);
        when(recommendationServiceProvider.getUserPurchasedProductIds(userId)).thenReturn(purchasedIds);

        Map<ProductCategory, Long> preferences = Map.of(ProductCategory.FRUITS, 5L);
        when(recommendationServiceProvider.getUserCategoryPreferences(userId)).thenReturn(preferences);

        List<Product> fruitsProducts = List.of(testProduct1, testProduct2);
        when(recommendationServiceProvider.findProductsByCategoryAndStatus(ProductCategory.FRUITS, ProductStatus.ACTIVE))
                .thenReturn(fruitsProducts);

        // Mock popular for fallback
        Map<Long, Long> popularCounts = Map.of(3L, 10L);
        when(recommendationServiceProvider.getPopularProductCounts()).thenReturn(popularCounts);

        List<Product> allProducts = List.of(testProduct2);
        when(recommendationServiceProvider.findAllProducts(0, Integer.MAX_VALUE)).thenReturn(allProducts);

        // When
        List<Product> recommendations = recommendationService.getPersonalizedRecommendations(userId, limit);

        // Then
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.contains(testProduct2)); // From fruits, not purchased (id 2)
        verify(recommendationServiceProvider).getUserPurchasedProductIds(userId);
        verify(recommendationServiceProvider).getUserCategoryPreferences(userId);
        verify(recommendationServiceProvider).findProductsByCategoryAndStatus(ProductCategory.FRUITS, ProductStatus.ACTIVE);
        verify(recommendationServiceProvider, times(1)).findAllProducts(anyInt(), anyInt());
    }

    @Test
    void getPersonalizedRecommendations_NoPurchaseHistory_ShouldReturnPopularProducts() {
        // Given
        Long userId = 1L;
        int limit = 3;
        when(recommendationServiceProvider.getUserPurchasedProductIds(userId)).thenReturn(List.of());

        Map<Long, Long> popularCounts = Map.of(1L, 10L);
        when(recommendationServiceProvider.getPopularProductCounts()).thenReturn(popularCounts);

        List<Product> allProducts = List.of(testProduct1);
        when(recommendationServiceProvider.findAllProducts(0, Integer.MAX_VALUE)).thenReturn(allProducts);

        // When
        List<Product> recommendations = recommendationService.getPersonalizedRecommendations(userId, limit);

        // Then
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.contains(testProduct1));
        verify(recommendationServiceProvider).getUserPurchasedProductIds(userId);
        verify(recommendationServiceProvider, never()).getUserCategoryPreferences(anyLong());
        verify(recommendationServiceProvider).getPopularProductCounts();
        verify(recommendationServiceProvider).findAllProducts(0, Integer.MAX_VALUE);
    }

    @Test
    void getCartBasedRecommendations_WithCart_ShouldReturnComplementaryProducts() {
        // Given
        Long userId = 1L;
        int limit = 4;
        List<Long> cartIds = List.of(1L);
        when(recommendationServiceProvider.getUserCartProductIds(userId)).thenReturn(cartIds);

        List<ProductCategory> cartCategories = List.of(ProductCategory.FRUITS);
        when(recommendationServiceProvider.getUserCartCategories(userId)).thenReturn(cartCategories);

        // Mock for other categories to empty
        when(recommendationServiceProvider.findProductsByCategoryAndStatus(any(ProductCategory.class), eq(ProductStatus.ACTIVE)))
                .thenReturn(List.of());

        List<Product> vegProducts = List.of(testProduct2);
        doReturn(vegProducts).when(recommendationServiceProvider).findProductsByCategoryAndStatus(ProductCategory.VEGETABLES, ProductStatus.ACTIVE);

        // When
        List<Product> recommendations = recommendationService.getCartBasedRecommendations(userId, limit);

        // Then
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.contains(testProduct2));
        verify(recommendationServiceProvider).getUserCartProductIds(userId);
        verify(recommendationServiceProvider).getUserCartCategories(userId);
        verify(recommendationServiceProvider).findProductsByCategoryAndStatus(ProductCategory.VEGETABLES, ProductStatus.ACTIVE);
    }

    @Test
    void getCartBasedRecommendations_EmptyCart_ShouldFallbackToPersonalized() {
        // Given
        Long userId = 1L;
        int limit = 3;
        when(recommendationServiceProvider.getUserCartProductIds(userId)).thenReturn(List.of());

        // The fallback calls getPersonalizedRecommendations, which uses provider mocks
        List<Long> purchasedIds = List.of();
        when(recommendationServiceProvider.getUserPurchasedProductIds(userId)).thenReturn(purchasedIds);

        Map<Long, Long> popularCounts = Map.of(1L, 10L);
        when(recommendationServiceProvider.getPopularProductCounts()).thenReturn(popularCounts);

        List<Product> allProducts = List.of(testProduct1);
        when(recommendationServiceProvider.findAllProducts(0, Integer.MAX_VALUE)).thenReturn(allProducts);

        // When
        List<Product> recommendations = recommendationService.getCartBasedRecommendations(userId, limit);

        // Then
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.contains(testProduct1));
        verify(recommendationServiceProvider).getUserCartProductIds(userId);
    }

    @Test
    void getCategoryRecommendations_ShouldReturnInStockProducts() {
        // Given
        ProductCategory category = ProductCategory.FRUITS;
        int limit = 2;
        List<Product> categoryProducts = List.of(testProduct1, testProduct2);
        when(recommendationServiceProvider.findProductsByCategoryAndStatus(category, ProductStatus.ACTIVE))
                .thenReturn(categoryProducts);

        // When
        List<Product> recommendations = recommendationService.getCategoryRecommendations(category, limit);

        // Then
        assertEquals(2, recommendations.size());
        assertTrue(recommendations.contains(testProduct1));
        verify(recommendationServiceProvider).findProductsByCategoryAndStatus(category, ProductStatus.ACTIVE);
    }

    @Test
    void getPopularProducts_ShouldReturnSortedActiveInStockProducts() {
        // Given
        int limit = 2;
        Map<Long, Long> purchaseCounts = Map.of(1L, 10L);
        when(recommendationServiceProvider.getPopularProductCounts()).thenReturn(purchaseCounts);

        List<Product> allProducts = List.of(testProduct1);
        when(recommendationServiceProvider.findAllProducts(0, Integer.MAX_VALUE)).thenReturn(allProducts);

        // When
        List<Product> popular = recommendationService.getPopularProducts(limit);

        // Then
        assertEquals(1, popular.size());
        assertEquals(testProduct1, popular.get(0));
        verify(recommendationServiceProvider).getPopularProductCounts();
        verify(recommendationServiceProvider).findAllProducts(0, Integer.MAX_VALUE);
    }

    @Test
    void getFrequentlyBoughtTogether_ShouldReturnSortedCoPurchasedProducts() {
        // Given
        Long productId = 1L;
        int limit = 2;
        Map<Long, Long> coPurchases = Map.of(2L, 8L);
        when(recommendationServiceProvider.getFrequentlyBoughtTogetherProductCounts(productId)).thenReturn(coPurchases);

        List<Product> allProducts = List.of(testProduct1, testProduct2);
        when(recommendationServiceProvider.findAllProducts(0, Integer.MAX_VALUE)).thenReturn(allProducts);

        // When
        List<Product> recommendations = recommendationService.getFrequentlyBoughtTogether(productId, limit);

        // Then
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.contains(testProduct2));
        verify(recommendationServiceProvider).getFrequentlyBoughtTogetherProductCounts(productId);
        verify(recommendationServiceProvider).findAllProducts(0, Integer.MAX_VALUE);
    }

    @Test
    void getUserPreferredCategoriesRecommendations_ShouldDelegateToPersonalized() {
        // Given
        Long userId = 1L;
        int limit = 3;
        List<Long> purchasedIds = List.of();
        when(recommendationServiceProvider.getUserPurchasedProductIds(userId)).thenReturn(purchasedIds);

        Map<Long, Long> popularCounts = Map.of(1L, 10L);
        when(recommendationServiceProvider.getPopularProductCounts()).thenReturn(popularCounts);

        List<Product> allProducts = List.of(testProduct1);
        when(recommendationServiceProvider.findAllProducts(0, Integer.MAX_VALUE)).thenReturn(allProducts);

        // When
        List<Product> recommendations = recommendationService.getUserPreferredCategoriesRecommendations(userId, limit);

        // Then
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.contains(testProduct1));
        verify(recommendationServiceProvider).getUserPurchasedProductIds(userId);
        verify(recommendationServiceProvider).getPopularProductCounts();
        verify(recommendationServiceProvider).findAllProducts(0, Integer.MAX_VALUE);
    }
}
