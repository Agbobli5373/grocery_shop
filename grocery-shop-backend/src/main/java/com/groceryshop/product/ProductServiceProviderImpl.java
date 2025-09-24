package com.groceryshop.product;

import com.groceryshop.product.spi.ProductServiceProvider;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of the ProductServiceProvider SPI.
 * This provides access to product functionality for other modules.
 */
@Service
public class ProductServiceProviderImpl implements ProductServiceProvider {

    private final ProductRepository productRepository;

    public ProductServiceProviderImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Optional<Product> findProductById(Long productId) {
        return productRepository.findById(productId);
    }

    @Override
    public void updateProductStock(Long productId, Integer newStockQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        product.setStockQuantity(newStockQuantity);
        productRepository.save(product);
    }

    @Override
    public java.util.List<Product> findAllProducts(int page, int size) {
        return productRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size)).getContent();
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    public long countProductsByStatusAndStockGreaterThan(com.groceryshop.product.ProductStatus status, int minStock) {
        return productRepository.countByStatusAndStockQuantityGreaterThan(status, minStock);
    }

    @Override
    public long countProductsByStockLessThan(int stockThreshold) {
        return productRepository.countByStockQuantityLessThan(stockThreshold);
    }

    @Override
    public long countProductsByStockQuantity(int stockQuantity) {
        return productRepository.countByStockQuantity(stockQuantity);
    }

    @Override
    public java.util.List<Product> findProductsByStockLessThan(int stockThreshold) {
        return productRepository.findByStockQuantityLessThan(stockThreshold);
    }
}
