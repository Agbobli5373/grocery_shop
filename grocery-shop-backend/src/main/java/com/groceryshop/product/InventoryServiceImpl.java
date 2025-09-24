package com.groceryshop.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the InventoryService interface.
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private static final Integer LOW_STOCK_THRESHOLD = 10; // Configurable threshold

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InventoryServiceImpl(
            ProductRepository productRepository,
            ApplicationEventPublisher eventPublisher) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        Integer oldStock = product.getStockQuantity();
        Integer newStock = oldStock + quantity;

        if (newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }

        product.setStockQuantity(newStock);
        productRepository.save(product);

        // Publish stock update event
        eventPublisher.publishEvent(new StockUpdatedEvent(
            this,
            productId,
            product.getName(),
            quantity,
            newStock
        ));

        // Check for low stock alert
        checkLowStockAlert(product);

        log.info("Updated stock for product {}: {} -> {}", productId, oldStock, newStock);
    }

    @Override
    public Integer getStockLevel(Long productId) {
        return productRepository.findById(productId)
                .map(Product::getStockQuantity)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
    }

    @Override
    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts(ProductStatus.ACTIVE, LOW_STOCK_THRESHOLD);
    }

    @Override
    public InventoryForecast getInventoryForecast(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Simple forecasting logic - in a real application, this would use historical data
        // and more sophisticated algorithms
        Integer averageDailySales = calculateAverageDailySales(productId);
        Integer forecastedDemand = averageDailySales != null ? averageDailySales * 30 : 0; // 30-day forecast
        Integer recommendedQuantity = Math.max(forecastedDemand - product.getStockQuantity(), 0);

        LocalDate nextRestockDate = LocalDate.now().plusDays(7); // Default to 1 week

        return new InventoryForecast(
            productId,
            product.getName(),
            product.getStockQuantity(),
            averageDailySales,
            forecastedDemand,
            nextRestockDate,
            recommendedQuantity,
            BigDecimal.valueOf(0.85) // 85% confidence level
        );
    }

    @Override
    public boolean hasSufficientStock(Long productId, Integer requestedQuantity) {
        Integer currentStock = getStockLevel(productId);
        return currentStock >= requestedQuantity;
    }

    private void checkLowStockAlert(Product product) {
        if (product.getStockQuantity() <= LOW_STOCK_THRESHOLD) {
            eventPublisher.publishEvent(new LowStockAlertEvent(
                product.getId(),
                product.getName(),
                product.getStockQuantity(),
                LOW_STOCK_THRESHOLD,
                LocalDateTime.now()
            ));

            log.warn("Low stock alert for product {}: {} units remaining (threshold: {})",
                    product.getName(), product.getStockQuantity(), LOW_STOCK_THRESHOLD);
        }
    }

    private Integer calculateAverageDailySales(Long productId) {
        // This is a simplified calculation. In a real application, you would:
        // 1. Query order history for the past 30-90 days
        // 2. Calculate daily averages
        // 3. Apply seasonal adjustments
        // 4. Use statistical forecasting methods

        // For now, return a mock value based on current stock levels
        // This would be replaced with actual historical data analysis
        return 5; // Mock average daily sales
    }
}
