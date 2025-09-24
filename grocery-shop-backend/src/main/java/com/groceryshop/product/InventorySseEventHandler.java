package com.groceryshop.product;

import com.groceryshop.sse.SseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Event handler that bridges RabbitMQ inventory events to SSE streams.
 */
@Component
public class InventorySseEventHandler {

    private final SseService sseService;

    public InventorySseEventHandler(SseService sseService) {
        this.sseService = sseService;
    }

    @RabbitListener(queues = "inventory-events")
    public void handleLowStockAlert(LowStockAlertEvent event) {
        // Send to admin inventory alerts stream
        String adminEmitterId = "inventory-admin-alerts";
        if (sseService.hasEmitter(adminEmitterId)) {
            sseService.sendEvent(adminEmitterId, new com.groceryshop.sse.SseEvent(
                "low-stock-alert",
                new LowStockAlertData(event.productId(), event.productName(), event.currentStock(), event.threshold())
            ));
        }
    }

    @RabbitListener(queues = "inventory-events")
    public void handleStockUpdated(StockUpdatedEvent event) {
        // Send to admin inventory alerts stream
        String adminEmitterId = "inventory-admin-alerts";
        if (sseService.hasEmitter(adminEmitterId)) {
            sseService.sendEvent(adminEmitterId, new com.groceryshop.sse.SseEvent(
                "stock-updated",
                new StockUpdateData(event.getProductId(), event.getProductName(), event.getNewStock(), event.getOldStock())
            ));
        }
    }

    /**
     * DTO for low stock alert events in SSE.
     */
    public static class LowStockAlertData {
        private Long productId;
        private String productName;
        private int currentStock;
        private int threshold;
        private java.time.LocalDateTime timestamp;

        public LowStockAlertData(Long productId, String productName, int currentStock, int threshold) {
            this.productId = productId;
            this.productName = productName;
            this.currentStock = currentStock;
            this.threshold = threshold;
            this.timestamp = java.time.LocalDateTime.now();
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getCurrentStock() {
            return currentStock;
        }

        public int getThreshold() {
            return threshold;
        }

        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    /**
     * DTO for stock update events in SSE.
     */
    public static class StockUpdateData {
        private Long productId;
        private String productName;
        private int newStock;
        private int oldStock;
        private java.time.LocalDateTime timestamp;

        public StockUpdateData(Long productId, String productName, int newStock, int oldStock) {
            this.productId = productId;
            this.productName = productName;
            this.newStock = newStock;
            this.oldStock = oldStock;
            this.timestamp = java.time.LocalDateTime.now();
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getNewStock() {
            return newStock;
        }

        public int getOldStock() {
            return oldStock;
        }

        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
