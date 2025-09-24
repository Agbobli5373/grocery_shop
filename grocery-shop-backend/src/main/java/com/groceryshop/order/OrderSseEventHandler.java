package com.groceryshop.order;

import com.groceryshop.sse.SseService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Event handler that bridges RabbitMQ order events to SSE streams.
 */
@Component
public class OrderSseEventHandler {

    private final SseService sseService;
    private final OrderRepository orderRepository;

    public OrderSseEventHandler(SseService sseService, OrderRepository orderRepository) {
        this.sseService = sseService;
        this.orderRepository = orderRepository;
    }

    @RabbitListener(queues = "order-events")
    public void handleOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        // Get the order to find customer ID
        Order order = orderRepository.findById(event.orderId()).orElse(null);
        if (order == null) return;

        Long customerId = order.getCustomer().getId();

        // Send to specific order tracking emitter
        String orderEmitterId = "order-" + event.orderId() + "-user-" + customerId;
        if (sseService.hasEmitter(orderEmitterId)) {
            sseService.sendEvent(orderEmitterId, new com.groceryshop.sse.SseEvent(
                "order-status-update",
                new OrderStatusEvent(event.orderId(), event.newStatus(), event.updatedAt())
            ));
        }

        // Also send to user's general notification stream
        String userEmitterId = "notifications-user-" + customerId;
        if (sseService.hasEmitter(userEmitterId)) {
            sseService.sendEvent(userEmitterId, new com.groceryshop.sse.SseEvent(
                "order-notification",
                new OrderNotificationEvent(
                    "Order Status Update",
                    "Your order #" + event.orderId() + " status changed to " + event.newStatus(),
                    event.orderId(),
                    event.newStatus(),
                    event.updatedAt()
                )
            ));
        }
    }

    @RabbitListener(queues = "order-events")
    public void handleOrderCreated(OrderCreatedEvent event) {
        // Send to user's general notification stream
        String userEmitterId = "notifications-user-" + event.customerId();
        if (sseService.hasEmitter(userEmitterId)) {
            // For order created, we need to determine the initial status
            Order order = orderRepository.findById(event.orderId()).orElse(null);
            OrderStatus status = order != null ? order.getStatus() : OrderStatus.PENDING;

            sseService.sendEvent(userEmitterId, new com.groceryshop.sse.SseEvent(
                "order-notification",
                new OrderNotificationEvent(
                    "Order Created",
                    "Your order #" + event.orderId() + " has been successfully created",
                    event.orderId(),
                    status,
                    event.orderDate()
                )
            ));
        }
    }

    /**
     * DTO for order status events in SSE.
     */
    public static class OrderStatusEvent {
        private Long orderId;
        private OrderStatus status;
        private java.time.LocalDateTime timestamp;

        public OrderStatusEvent(Long orderId, OrderStatus status, java.time.LocalDateTime timestamp) {
            this.orderId = orderId;
            this.status = status;
            this.timestamp = timestamp;
        }

        public Long getOrderId() {
            return orderId;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    /**
     * DTO for order notification events in SSE.
     */
    public static class OrderNotificationEvent {
        private String title;
        private String message;
        private Long orderId;
        private OrderStatus status;
        private java.time.LocalDateTime timestamp;

        public OrderNotificationEvent(String title, String message, Long orderId, OrderStatus status, java.time.LocalDateTime timestamp) {
            this.title = title;
            this.message = message;
            this.orderId = orderId;
            this.status = status;
            this.timestamp = timestamp;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public Long getOrderId() {
            return orderId;
        }

        public OrderStatus getStatus() {
            return status;
        }

        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
