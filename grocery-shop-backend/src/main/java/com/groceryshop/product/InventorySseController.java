package com.groceryshop.product;

import com.groceryshop.admin.AdminService;
import com.groceryshop.sse.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE Controller for real-time inventory alerts and updates.
 */
@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory SSE", description = "Server-Sent Events for inventory alerts")
public class InventorySseController {

    private final SseService sseService;
    private final AdminService adminService;

    public InventorySseController(SseService sseService, AdminService adminService) {
        this.sseService = sseService;
        this.adminService = adminService;
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Subscribe to inventory alerts", description = "Receive real-time inventory alerts and stock updates via SSE")
    public SseEmitter subscribeToInventoryAlerts() {
        String emitterId = "inventory-admin-alerts";
        SseEmitter emitter = sseService.createEmitter(emitterId);

        // Send initial inventory status
        AdminService.InventoryStatus status = adminService.getInventoryStatus();
        sseService.sendEvent(emitterId, new com.groceryshop.sse.SseEvent(
            "inventory-status",
            new InventoryStatusEvent((int)status.totalProducts(), (int)status.lowStockProducts(), (int)status.outOfStockProducts())
        ));

        return emitter;
    }

    /**
     * DTO for inventory status events.
     */
    public static class InventoryStatusEvent {
        private int totalProducts;
        private int lowStockProducts;
        private int outOfStockProducts;
        private java.time.LocalDateTime timestamp;

        public InventoryStatusEvent(int totalProducts, int lowStockProducts, int outOfStockProducts) {
            this.totalProducts = totalProducts;
            this.lowStockProducts = lowStockProducts;
            this.outOfStockProducts = outOfStockProducts;
            this.timestamp = java.time.LocalDateTime.now();
        }

        public int getTotalProducts() {
            return totalProducts;
        }

        public int getLowStockProducts() {
            return lowStockProducts;
        }

        public int getOutOfStockProducts() {
            return outOfStockProducts;
        }

        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    /**
     * DTO for low stock alert events.
     */
    public static class LowStockAlertEvent {
        private Long productId;
        private String productName;
        private int currentStock;
        private int threshold;
        private java.time.LocalDateTime timestamp;

        public LowStockAlertEvent(Long productId, String productName, int currentStock, int threshold) {
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
}
