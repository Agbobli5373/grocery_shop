package com.groceryshop.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the OrderService interface.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Long customerId, String deliveryAddress) {
        log.info("Creating order for customer ID: {} with delivery address: {}", customerId, deliveryAddress);

        // This is a basic implementation - in a real application, this would
        // involve getting the customer's cart, calculating totals, etc.
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(deliveryAddress);

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getId());

        return savedOrder;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        // This would need to be implemented based on your Order entity relationship
        // For now, returning empty list
        return List.of();
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);

        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated from {} to {}", id, oldStatus, status);

        return updatedOrder;
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order {} cancelled", id);
    }

    @Override
    public Order trackOrder(Long id) {
        return getOrderById(id);
    }
}
