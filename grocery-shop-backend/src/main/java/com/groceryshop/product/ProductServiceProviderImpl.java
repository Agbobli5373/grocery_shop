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
}
