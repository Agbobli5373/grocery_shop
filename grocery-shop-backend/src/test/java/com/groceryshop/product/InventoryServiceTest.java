package com.groceryshop.product;

import com.groceryshop.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InventoryServiceImpl.
 * Tests inventory management operations including stock updates, forecasting, and alerts.
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private InventoryService inventoryService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl(productRepository, eventPublisher);
        testProduct = TestDataFactory.createTestProduct();
        testProduct.setStockQuantity(100); // Set initial stock
    }

    @Test
    void updateStock_ShouldIncreaseStockAndPublishEvent_WhenValidRequest() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        inventoryService.updateStock(1L, 50);

        // Then
        assertEquals(150, testProduct.getStockQuantity());
        verify(productRepository).save(testProduct);

        // Verify StockUpdatedEvent
        ArgumentCaptor<StockUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(StockUpdatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        StockUpdatedEvent event = eventCaptor.getValue();
        assertEquals(1L, event.getProductId());
        assertEquals(testProduct.getName(), event.getProductName());
        assertEquals(100, event.getOldStock()); // Original stock
        assertEquals(150, event.getNewStock()); // Updated stock
    }

    @Test
    void updateStock_ShouldDecreaseStockAndPublishEvent_WhenValidRequest() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        inventoryService.updateStock(1L, -30);

        // Then
        assertEquals(70, testProduct.getStockQuantity());
        verify(productRepository).save(testProduct);

        // Verify StockUpdatedEvent
        ArgumentCaptor<StockUpdatedEvent> eventCaptor = ArgumentCaptor.forClass(StockUpdatedEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        StockUpdatedEvent event = eventCaptor.getValue();
        assertEquals(100, event.getOldStock()); // Original stock
        assertEquals(70, event.getNewStock()); // Updated stock
    }

    @Test
    void updateStock_ShouldPublishLowStockAlert_WhenStockBelowThreshold() {
        // Given
        testProduct.setStockQuantity(15); // Above threshold initially
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When - reduce stock to below threshold (10)
        inventoryService.updateStock(1L, -10);

        // Then
        assertEquals(5, testProduct.getStockQuantity());

        // Verify both StockUpdatedEvent and LowStockAlertEvent were published
        ArgumentCaptor<StockUpdatedEvent> stockEventCaptor = ArgumentCaptor.forClass(StockUpdatedEvent.class);
        ArgumentCaptor<LowStockAlertEvent> alertCaptor = ArgumentCaptor.forClass(LowStockAlertEvent.class);

        verify(eventPublisher).publishEvent(stockEventCaptor.capture());
        verify(eventPublisher).publishEvent(alertCaptor.capture());

        StockUpdatedEvent stockEvent = stockEventCaptor.getValue();
        assertEquals(1L, stockEvent.getProductId());
        assertEquals(15, stockEvent.getOldStock()); // Original stock before update
        assertEquals(5, stockEvent.getNewStock()); // Updated stock

        LowStockAlertEvent alert = alertCaptor.getValue();
        assertEquals(1L, alert.productId());
        assertEquals(5, alert.currentStock());
        assertEquals(10, alert.threshold());
    }

    @Test
    void updateStock_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> inventoryService.updateStock(999L, 50));
        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
        verify(productRepository, never()).save(any(Product.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void updateStock_ShouldThrowException_WhenResultingStockNegative() {
        // Given
        testProduct.setStockQuantity(20);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> inventoryService.updateStock(1L, -50));
        assertEquals("Stock cannot be negative", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void getStockLevel_ShouldReturnStockQuantity_WhenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Integer stockLevel = inventoryService.getStockLevel(1L);

        // Then
        assertEquals(100, stockLevel);
        verify(productRepository).findById(1L);
    }

    @Test
    void getStockLevel_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> inventoryService.getStockLevel(999L));
        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
    }

    @Test
    void getLowStockProducts_ShouldReturnProductsBelowThreshold() {
        // Given
        List<Product> lowStockProducts = Arrays.asList(testProduct);
        when(productRepository.findLowStockProducts(ProductStatus.ACTIVE, 10)).thenReturn(lowStockProducts);

        // When
        List<Product> result = inventoryService.getLowStockProducts();

        // Then
        assertEquals(1, result.size());
        assertEquals(testProduct.getId(), result.get(0).getId());
        verify(productRepository).findLowStockProducts(ProductStatus.ACTIVE, 10);
    }

    @Test
    void getInventoryForecast_ShouldReturnForecast_WhenProductExists() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        InventoryForecast forecast = inventoryService.getInventoryForecast(1L);

        // Then
        assertNotNull(forecast);
        assertEquals(1L, forecast.productId());
        assertEquals(testProduct.getName(), forecast.productName());
        assertEquals(100, forecast.currentStock());
        assertEquals(5, forecast.averageDailySales()); // Mock value from implementation
        assertEquals(150, forecast.forecastedDemand()); // 5 * 30 days
        assertEquals(50, forecast.recommendedRestockQuantity()); // max(150 - 100, 0)
        assertEquals(BigDecimal.valueOf(0.85), forecast.confidenceLevel());
        assertEquals(LocalDate.now().plusDays(7), forecast.nextRestockDate());
    }

    @Test
    void getInventoryForecast_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> inventoryService.getInventoryForecast(999L));
        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
    }

    @Test
    void hasSufficientStock_ShouldReturnTrue_WhenStockIsSufficient() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        boolean result = inventoryService.hasSufficientStock(1L, 50);

        // Then
        assertTrue(result);
        verify(productRepository).findById(1L);
    }

    @Test
    void hasSufficientStock_ShouldReturnFalse_WhenStockIsInsufficient() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        boolean result = inventoryService.hasSufficientStock(1L, 150);

        // Then
        assertFalse(result);
        verify(productRepository).findById(1L);
    }

    @Test
    void hasSufficientStock_ShouldReturnTrue_WhenStockEqualsRequestedQuantity() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        boolean result = inventoryService.hasSufficientStock(1L, 100);

        // Then
        assertTrue(result);
        verify(productRepository).findById(1L);
    }

    @Test
    void hasSufficientStock_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> inventoryService.hasSufficientStock(999L, 50));
        assertTrue(exception.getMessage().contains("Product not found with id: 999"));
    }
}
