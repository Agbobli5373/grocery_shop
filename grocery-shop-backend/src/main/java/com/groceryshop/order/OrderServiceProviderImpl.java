package com.groceryshop.order;

import com.groceryshop.order.spi.OrderServiceProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the OrderServiceProvider SPI.
 * This provides access to order functionality for other modules.
 */
@Service
public class OrderServiceProviderImpl implements OrderServiceProvider {

    private final OrderRepository orderRepository;

    public OrderServiceProviderImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Optional<Order> findOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> findAllOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    @Override
    public long countOrders() {
        return orderRepository.count();
    }

    @Override
    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByStatus(status);
    }

    @Override
    public Optional<BigDecimal> sumTotalAmountByStatus(OrderStatus status) {
        return orderRepository.sumTotalAmountByStatus(status);
    }

    @Override
    public long countOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.countByOrderDateBetween(startDate, endDate);
    }

    @Override
    public long countOrdersByStatusAndDateRange(OrderStatus status, LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.countByStatusAndOrderDateBetween(status, startDate, endDate);
    }

    @Override
    public List<Order> findOrdersByDateRangeAndStatus(LocalDateTime startDate, LocalDateTime endDate, OrderStatus status) {
        return orderRepository.findByOrderDateBetweenAndStatus(startDate, endDate, status);
    }

    @Override
    public List<com.groceryshop.auth.User> findDistinctCustomersWithOrdersAfter(LocalDateTime since) {
        return orderRepository.findDistinctCustomersWithOrdersAfter(since);
    }

    @Override
    public List<Object[]> findTopCustomersByOrderCount(int limit) {
        return orderRepository.findTopCustomersByOrderCount(limit);
    }
}
