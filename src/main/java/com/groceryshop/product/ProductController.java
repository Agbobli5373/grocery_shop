package com.groceryshop.product;

import com.groceryshop.shared.dto.request.CreateProductRequest;
import com.groceryshop.shared.dto.request.UpdateProductRequest;
import com.groceryshop.shared.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management APIs")
@SecurityRequirement(name = "bearer-jwt")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a paginated list of products with optional filtering")
    public ResponseEntity<List<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) ProductStatus status,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") String sortDirection) {

        ProductSearchCriteria criteria = new ProductSearchCriteria(
            name, category, status, null, null, page, size, sortBy, sortDirection
        );

        List<Product> products = productService.getAllProducts(criteria);
        List<ProductResponse> responses = products.stream()
            .map(this::mapToResponse)
            .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(mapToResponse(product));
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Create a new product (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        Product product = productService.createProduct(request);
        return ResponseEntity.ok(mapToResponse(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(mapToResponse(product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Delete a product (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String query) {
        List<Product> products = productService.searchProducts(query);
        List<ProductResponse> responses = products.stream()
            .map(this::mapToResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable ProductCategory category) {
        List<Product> products = productService.getProductsByCategory(category);
        List<ProductResponse> responses = products.stream()
            .map(this::mapToResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<ProductResponse>> getRecommendations(@RequestParam Long userId) {
        List<Product> products = productService.getRecommendations(userId);
        List<ProductResponse> responses = products.stream()
            .map(this::mapToResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }

    private ProductResponse mapToResponse(Product product) {
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
