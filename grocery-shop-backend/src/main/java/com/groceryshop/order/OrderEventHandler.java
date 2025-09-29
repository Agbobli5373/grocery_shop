package com.groceryshop.order;

import com.groceryshop.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event handler for order-related events.
 * Processes order lifecycle events and coordinates with other modules.
 */
@Component
public class OrderEventHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderEventHandler.class);

    private final OrderService orderService;

    public OrderEventHandler(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Handles order created events.
     * Publishes order processing event to trigger fulfillment.
     */
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("Processing order created event for order ID: {}", event.orderId());

        try {
            // Update order status to process
            orderService.updateOrderStatus(event.orderId(), OrderStatus.PROCESSING);

            // Publish order processing event
            // This would trigger inventory updates and notifications
            log.info("Order {} moved to processing status", event.orderId());

        } catch (Exception e) {
            log.error("Failed to process order created event for order ID: {}", event.orderId(), e);
            // Could implement compensation logic here
        }
    }

    /**
     * Handles order status update events.
     */
    @EventListener
    public void handleOrderStatusUpdated(OrderStatusUpdatedEvent event) {
        log.info("Processing order status update for order ID: {} to status: {}",
                event.orderId(), event.newStatus());

        // Additional business logic for status changes
        switch (event.newStatus()) {
            case CONFIRMED -> log.info("Order {} confirmed, payment processed", event.orderId());
            case PROCESSING -> log.info("Order {} is being prepared", event.orderId());
            case SHIPPED -> log.info("Order {} has been shipped", event.orderId());
            case DELIVERED -> log.info("Order {} has been delivered", event.orderId());
            case CANCELLED -> log.info("Order {} has been cancelled", event.orderId());
            default -> throw new IllegalArgumentException("Unexpected value: " + event.newStatus());
        }
    }

    /**
     * RabbitMQ listener for order processing queue.
     * This could be used for asynchronous order processing.
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_PROCESSING_QUEUE)
    public void processOrderFromQueue(OrderCreatedEvent event) {
        log.info("Received order processing message from queue for order ID: {}", event.orderId());

        try {
            // Process order asynchronously
            // This could include inventory checks, payment processing, etc.
            orderService.updateOrderStatus(event.orderId(), OrderStatus.CONFIRMED);

            log.info("Order {} processed successfully from queue", event.orderId());

        } catch (Exception e) {
            log.error("Failed to process order from queue for order ID: {}", event.orderId(), e);
            // Could implement dead letter queue handling here
        }
    }
}
