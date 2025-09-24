package com.groceryshop.product;

import com.groceryshop.TestDataFactory;
import com.groceryshop.product.spi.ProductServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * SPI Provider Contract Test for ProductServiceProvider.
 * Tests the contract implementation to ensure SPI compatibility.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceProviderTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceProvider productServiceProvider;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productServiceProvider = new ProductServiceProviderImpl(productRepository);
        testProduct = TestDataFactory.createTestProduct();
    }

    @Test
    void findProductById_ShouldReturnProduct_WhenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productServiceProvider.findProductById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testProduct.getId(), result.get().getId());
        assertEquals(testProduct.getName(), result.get().getName());
    }

    @Test
    void findProductById_ShouldReturnEmpty_WhenProductDoesNotExist() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productServiceProvider.findProductById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findAllProducts_ShouldReturnPagedProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> page = new PageImpl<>(products);
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        List<Product> result = productServiceProvider.findAllProducts(0, 10);

        // Then
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productRepository).findAll(PageRequest.of(0, 10));
    }

    @Test
    void countProducts_ShouldReturnTotalCount() {
        // Given
        when(productRepository.count()).thenReturn(42L);

        // When
        long result = productServiceProvider.countProducts();

        // Then
        assertEquals(42L, result);
    }

    @Test
    void countProductsByStatusAndStockGreaterThan_ShouldReturnCount() {
        // Given
        when(productRepository.countByStatusAndStockQuantityGreaterThan(ProductStatus.ACTIVE, 5)).thenReturn(15L);

        // When
        long result = productServiceProvider.countProductsByStatusAndStockGreaterThan(ProductStatus.ACTIVE, 5);

        // Then
        assertEquals(15L, result);
    }

    @Test
    void countProductsByStockLessThan_ShouldReturnCount() {
        // Given
        when(productRepository.countByStockQuantityLessThan(10)).thenReturn(8L);

        // When
        long result = productServiceProvider.countProductsByStockLessThan(10);

        // Then
        assertEquals(8L, result);
    }

    @Test
    void countProductsByStockQuantity_ShouldReturnCount() {
        // Given
        when(productRepository.countByStockQuantity(0)).thenReturn(3L);

        // When
        long result = productServiceProvider.countProductsByStockQuantity(0);

        // Then
        assertEquals(3L, result);
    }

    @Test
    void findProductsByStockLessThan_ShouldReturnProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByStockQuantityLessThan(10)).thenReturn(products);

        // When
        List<Product> result = productServiceProvider.findProductsByStockLessThan(10);

        // Then
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
    }

    @Test
    void updateProductStock_ShouldUpdateStock_WhenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        productServiceProvider.updateProductStock(1L, 50);

        // Then
        assertEquals(50, testProduct.getStockQuantity());
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProductStock_ShouldThrowException_WhenProductDoesNotExist() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> productServiceProvider.updateProductStock(999L, 50));
        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void findProductById_ShouldHandleNullId() {
        // Given
        when(productRepository.findById(null)).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productServiceProvider.findProductById(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findAllProducts_ShouldHandleNegativePage() {
        // When & Then - Spring Data will throw IllegalArgumentException for negative page
        assertThrows(IllegalArgumentException.class,
            () -> productServiceProvider.findAllProducts(-1, 10));
    }

    @Test
    void findAllProducts_ShouldHandleZeroSize() {
        // When & Then - Spring Data will throw IllegalArgumentException for zero page size
        assertThrows(IllegalArgumentException.class,
            () -> productServiceProvider.findAllProducts(0, 0));
    }
}
