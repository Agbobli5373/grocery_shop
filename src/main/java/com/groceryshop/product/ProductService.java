package com.groceryshop.product;

import com.groceryshop.shared.dto.request.CreateProductRequest;
import com.groceryshop.shared.dto.request.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts(ProductSearchCriteria criteria);
    Product getProductById(Long id);
    Product createProduct(CreateProductRequest request);
    Product updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);
    List<Product> getRecommendations(Long userId);
    List<Product> getProductsByCategory(ProductCategory category);
    List<Product> searchProducts(String query);
}
