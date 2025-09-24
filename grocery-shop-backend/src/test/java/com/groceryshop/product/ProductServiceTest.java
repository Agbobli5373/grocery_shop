package com.groceryshop.product;

import com.groceryshop.TestDataFactory;
import com.groceryshop.shared.dto.request.CreateProductRequest;
import com.groceryshop.shared.dto.request.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductServiceImpl.
 * Tests all business logic operations for product management.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ProductService productService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, eventPublisher);
        testProduct = TestDataFactory.createTestProduct();
    }

    @Test
    void getAllProducts_ShouldReturnFilteredProducts() {
        // Given
        ProductSearchCriteria criteria = new ProductSearchCriteria(
            "Apple", ProductCategory.FRUITS, ProductStatus.ACTIVE,
            BigDecimal.valueOf(10.0), BigDecimal.valueOf(50.0), 0, 10, "name", "ASC"
        );

        List<Product> products = Arrays.asList(testProduct);
        Page<Product> page = new PageImpl<>(products);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // When
        List<Product> result = productService.getAllProducts(criteria);

        // Then
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Product result = productService.getProductById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testProduct.getId(), result.getId());
        assertEquals(testProduct.getName(), result.getName());
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductDoesNotExist() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> productService.getProductById(999L));
        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
    }

    @Test
    void createProduct_ShouldCreateAndPublishEvent() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
            "New Product", "Description", BigDecimal.valueOf(29.99), "image.jpg",
            ProductCategory.PANTRY, 100
        );

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName(request.name());
        savedProduct.setStockQuantity(request.stockQuantity());

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = productService.createProduct(request);

        // Then
        assertNotNull(result);
        assertEquals(request.name(), result.getName());
        assertEquals(request.stockQuantity(), result.getStockQuantity());

        // Verify event publishing
        ArgumentCaptor<ProductAddedEvent> eventCaptor = ArgumentCaptor.forClass(ProductAddedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        ProductAddedEvent event = eventCaptor.getValue();
        assertEquals(savedProduct.getId(), event.getProductId());
        assertEquals(savedProduct.getName(), event.getProductName());
        assertEquals(savedProduct.getStockQuantity(), event.getInitialStock());
    }

    @Test
    void updateProduct_ShouldUpdateAllFieldsAndPublishEvents() {
        // Given
        UpdateProductRequest request = new UpdateProductRequest(
            "Updated Name", "Updated Description", BigDecimal.valueOf(39.99), "new-image.jpg",
            ProductCategory.DAIRY, ProductStatus.ACTIVE, 150
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.updateProduct(1L, request);

        // Then
        assertNotNull(result);
        assertEquals(request.name(), testProduct.getName());
        assertEquals(request.stockQuantity(), testProduct.getStockQuantity());

        // Verify stock updated event (stock changed from 100 to 150)
        ArgumentCaptor<StockUpdatedEvent> stockEventCaptor = ArgumentCaptor.forClass(StockUpdatedEvent.class);
        verify(eventPublisher).publishEvent(stockEventCaptor.capture());
        StockUpdatedEvent stockEvent = stockEventCaptor.getValue();
        assertEquals(100, stockEvent.getOldStock());
        assertEquals(150, stockEvent.getNewStock());

        // Verify product updated event
        ArgumentCaptor<ProductUpdatedEvent> productEventCaptor = ArgumentCaptor.forClass(ProductUpdatedEvent.class);
        verify(eventPublisher, times(2)).publishEvent(any()); // StockUpdatedEvent + ProductUpdatedEvent
    }

    @Test
    void updateProduct_ShouldNotPublishStockEvent_WhenStockUnchanged() {
        // Given
        UpdateProductRequest request = new UpdateProductRequest(
            "Updated Name", null, null, null, null, null, 100 // Same stock quantity
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        productService.updateProduct(1L, request);

        // Then
        // Should only publish ProductUpdatedEvent, not StockUpdatedEvent
        verify(eventPublisher, times(1)).publishEvent(any(ProductUpdatedEvent.class));
        verify(eventPublisher, never()).publishEvent(any(StockUpdatedEvent.class));
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductDoesNotExist() {
        // Given
        UpdateProductRequest request = new UpdateProductRequest("Name", null, null, null, null, null, null);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> productService.updateProduct(999L, request));
        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_ShouldDeleteExistingProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        productService.deleteProduct(1L);

        // Then
        verify(productRepository).delete(testProduct);
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductDoesNotExist() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> productService.deleteProduct(999L));
        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        verify(productRepository, never()).delete(any(Product.class));
    }

    @Test
    void getRecommendations_ShouldReturnTopProducts() {
        // Given
        List<Product> recommendations = Arrays.asList(testProduct);
        when(productRepository.findTop10ByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE))
            .thenReturn(recommendations);

        // When
        List<Product> result = productService.getRecommendations(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productRepository).findTop10ByStatusOrderByCreatedAtDesc(ProductStatus.ACTIVE);
    }

    @Test
    void getProductsByCategory_ShouldReturnFilteredProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryAndStatus(ProductCategory.FRUITS, ProductStatus.ACTIVE))
            .thenReturn(products);

        // When
        List<Product> result = productService.getProductsByCategory(ProductCategory.FRUITS);

        // Then
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productRepository).findByCategoryAndStatus(ProductCategory.FRUITS, ProductStatus.ACTIVE);
    }

    @Test
    void searchProducts_ShouldReturnMatchingProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByNameContainingIgnoreCaseAndStatus("apple", ProductStatus.ACTIVE))
            .thenReturn(products);

        // When
        List<Product> result = productService.searchProducts("apple");

        // Then
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productRepository).findByNameContainingIgnoreCaseAndStatus("apple", ProductStatus.ACTIVE);
    }

    @Test
    void getAllProducts_ShouldHandleEmptyCriteria() {
        // Given
        ProductSearchCriteria criteria = new ProductSearchCriteria(
            null, null, null, null, null, 0, 10, "name", "ASC"
        );

        List<Product> products = Arrays.asList(testProduct);
        Page<Product> page = new PageImpl<>(products);
        when(productRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        // When
        List<Product> result = productService.getAllProducts(criteria);

        // Then
        assertEquals(1, result.size());
        verify(productRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void createProduct_ShouldHandleNullImageUrl() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
            "Product", "Description", BigDecimal.valueOf(19.99), null, ProductCategory.PANTRY, 50
        );

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName(request.name());

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = productService.createProduct(request);

        // Then
        assertNotNull(result);
        assertEquals(request.name(), result.getName());
        verify(eventPublisher).publishEvent(any(ProductAddedEvent.class));
    }
}
