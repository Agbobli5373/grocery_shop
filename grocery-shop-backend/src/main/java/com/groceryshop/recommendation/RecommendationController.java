package com.groceryshop.recommendation;

import com.groceryshop.product.Product;
import com.groceryshop.product.ProductCategory;
import com.groceryshop.shared.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for product recommendations.
 */
@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendations", description = "Product recommendation endpoints")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/personalized")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get personalized recommendations for the authenticated user")
    public ResponseEntity<List<ProductResponse>> getPersonalizedRecommendations(
            Authentication authentication,
            @Parameter(description = "Maximum number of recommendations to return")
            @RequestParam(defaultValue = "10") int limit) {

        Long userId = getUserIdFromAuthentication(authentication);
        List<Product> recommendations = recommendationService.getPersonalizedRecommendations(userId, limit);

        List<ProductResponse> response = recommendations.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cart-based")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get recommendations based on current cart contents")
    public ResponseEntity<List<ProductResponse>> getCartBasedRecommendations(
            Authentication authentication,
            @Parameter(description = "Maximum number of recommendations to return")
            @RequestParam(defaultValue = "10") int limit) {

        Long userId = getUserIdFromAuthentication(authentication);
        List<Product> recommendations = recommendationService.getCartBasedRecommendations(userId, limit);

        List<ProductResponse> response = recommendations.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get recommendations for a specific product category")
    public ResponseEntity<List<ProductResponse>> getCategoryRecommendations(
            @Parameter(description = "Product category")
            @PathVariable ProductCategory category,
            @Parameter(description = "Maximum number of recommendations to return")
            @RequestParam(defaultValue = "10") int limit) {

        List<Product> recommendations = recommendationService.getCategoryRecommendations(category, limit);

        List<ProductResponse> response = recommendations.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    @Operation(summary = "Get popular/trending products")
    public ResponseEntity<List<ProductResponse>> getPopularProducts(
            @Parameter(description = "Maximum number of recommendations to return")
            @RequestParam(defaultValue = "10") int limit) {

        List<Product> recommendations = recommendationService.getPopularProducts(limit);

        List<ProductResponse> response = recommendations.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/frequently-bought-together/{productId}")
    @Operation(summary = "Get products frequently bought together with the specified product")
    public ResponseEntity<List<ProductResponse>> getFrequentlyBoughtTogether(
            @Parameter(description = "Product ID to find related products for")
            @PathVariable Long productId,
            @Parameter(description = "Maximum number of recommendations to return")
            @RequestParam(defaultValue = "5") int limit) {

        List<Product> recommendations = recommendationService.getFrequentlyBoughtTogether(productId, limit);

        List<ProductResponse> response = recommendations.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/preferred-categories")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get recommendations based on user's preferred categories")
    public ResponseEntity<List<ProductResponse>> getUserPreferredCategoriesRecommendations(
            Authentication authentication,
            @Parameter(description = "Maximum number of recommendations to return")
            @RequestParam(defaultValue = "10") int limit) {

        Long userId = getUserIdFromAuthentication(authentication);
        List<Product> recommendations = recommendationService.getUserPreferredCategoriesRecommendations(userId, limit);

        List<ProductResponse> response = recommendations.stream()
                .map(this::convertToProductResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        // Extract user ID from authentication - this would depend on your UserDetails implementation
        // For now, assuming the principal is a User entity or contains user ID
        if (authentication.getPrincipal() instanceof com.groceryshop.auth.User) {
            return ((com.groceryshop.auth.User) authentication.getPrincipal()).getId();
        }
        // Fallback - you might need to adjust this based on your authentication setup
        return Long.valueOf(authentication.getName());
    }

    private ProductResponse convertToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory(),
                product.getStatus(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
