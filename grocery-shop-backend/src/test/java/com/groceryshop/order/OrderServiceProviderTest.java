package com.groceryshop.order;

import com.groceryshop.TestDataFactory;
import com.groceryshop.auth.User;
import com.groceryshop.order.spi.OrderServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * SPI Provider Contract Test for OrderServiceProvider.
 * Tests the contract implementation to ensure SPI compatibility.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceProviderTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderServiceProvider orderServiceProvider;
    private Order testOrder;
    private User testUser;

    @BeforeEach
    void setUp() {
        orderServiceProvider = new OrderServiceProviderImpl(orderRepository);
        testOrder = TestDataFactory.createTestOrder();
        testUser = TestDataFactory.createTestUser();
    }

    @Test
    void findOrderById_ShouldReturnOrder_WhenOrderExists() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        Optional<Order> result = orderServiceProvider.findOrderById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testOrder.getId(), result.get().getId());
        assertEquals(testOrder.getStatus(), result.get().getStatus());
    }

    @Test
    void findOrderById_ShouldReturnEmpty_WhenOrderDoesNotExist() {
        // Given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderServiceProvider.findOrderById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findAllOrders_ShouldReturnOrders_WhenOrdersExist() {
        // Given
        List<Order> orders = List.of(testOrder);
        Page<Order> page = new PageImpl<>(orders, PageRequest.of(0, 10), orders.size());
        when(orderRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        List<Order> result = orderServiceProvider.findAllOrders(0, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder.getId(), result.get(0).getId());
    }

    @Test
    void findAllOrders_ShouldReturnEmptyList_WhenNoOrdersExist() {
        // Given
        Page<Order> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(orderRepository.findAll(any(PageRequest.class))).thenReturn(page);

        // When
        List<Order> result = orderServiceProvider.findAllOrders(0, 10);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void countOrders_ShouldReturnTotalCount() {
        // Given
        when(orderRepository.count()).thenReturn(5L);

        // When
        long result = orderServiceProvider.countOrders();

        // Then
        assertEquals(5L, result);
    }

    @Test
    void countOrdersByStatus_ShouldReturnCountForStatus() {
        // Given
        when(orderRepository.countByStatus(OrderStatus.PENDING)).thenReturn(3L);

        // When
        long result = orderServiceProvider.countOrdersByStatus(OrderStatus.PENDING);

        // Then
        assertEquals(3L, result);
    }

    @Test
    void sumTotalAmountByStatus_ShouldReturnSum_WhenOrdersExist() {
        // Given
        BigDecimal expectedSum = new BigDecimal("150.00");
        when(orderRepository.sumTotalAmountByStatus(OrderStatus.CONFIRMED)).thenReturn(Optional.of(expectedSum));

        // When
        Optional<BigDecimal> result = orderServiceProvider.sumTotalAmountByStatus(OrderStatus.CONFIRMED);

        // Then
        assertTrue(result.isPresent());
        assertEquals(expectedSum, result.get());
    }

    @Test
    void sumTotalAmountByStatus_ShouldReturnEmpty_WhenNoOrdersExist() {
        // Given
        when(orderRepository.sumTotalAmountByStatus(OrderStatus.CANCELLED)).thenReturn(Optional.empty());

        // When
        Optional<BigDecimal> result = orderServiceProvider.sumTotalAmountByStatus(OrderStatus.CANCELLED);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void countOrdersByDateRange_ShouldReturnCount() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(orderRepository.countByOrderDateBetween(startDate, endDate)).thenReturn(10L);

        // When
        long result = orderServiceProvider.countOrdersByDateRange(startDate, endDate);

        // Then
        assertEquals(10L, result);
    }

    @Test
    void countOrdersByStatusAndDateRange_ShouldReturnCount() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        when(orderRepository.countByStatusAndOrderDateBetween(OrderStatus.CONFIRMED, startDate, endDate)).thenReturn(7L);

        // When
        long result = orderServiceProvider.countOrdersByStatusAndDateRange(OrderStatus.CONFIRMED, startDate, endDate);

        // Then
        assertEquals(7L, result);
    }

    @Test
    void findOrdersByDateRangeAndStatus_ShouldReturnOrders() {
        // Given
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByOrderDateBetweenAndStatus(startDate, endDate, OrderStatus.CONFIRMED)).thenReturn(orders);

        // When
        List<Order> result = orderServiceProvider.findOrdersByDateRangeAndStatus(startDate, endDate, OrderStatus.CONFIRMED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder.getId(), result.get(0).getId());
    }

    @Test
    void findDistinctCustomersWithOrdersAfter_ShouldReturnUsers() {
        // Given
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<User> users = List.of(testUser);
        when(orderRepository.findDistinctCustomersWithOrdersAfter(since)).thenReturn(users);

        // When
        List<User> result = orderServiceProvider.findDistinctCustomersWithOrdersAfter(since);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
    }

    @Test
    void findTopCustomersByOrderCount_ShouldReturnCustomerData() {
        // Given
        Object[] customerData1 = new Object[]{testUser.getId(), "John Doe", 5L, new BigDecimal("250.00")};
        Object[] customerData2 = new Object[]{2L, "Jane Smith", 3L, new BigDecimal("150.00")};
        Object[] customerData3 = new Object[]{3L, "Bob Johnson", 2L, new BigDecimal("100.00")};
        Object[] customerData4 = new Object[]{4L, "Alice Brown", 1L, new BigDecimal("50.00")};
        @SuppressWarnings("unchecked")
        List<Object[]> customerDataList = (List<Object[]>) (List<?>) List.of(customerData1, customerData2, customerData3, customerData4);
        when(orderRepository.findTopCustomersByOrderCount(5)).thenReturn(customerDataList);

        // When
        List<Object[]> result = orderServiceProvider.findTopCustomersByOrderCount(5);

        // Then
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(testUser.getId(), result.get(0)[0]);
        assertEquals(5L, result.get(0)[2]);
        assertEquals(new BigDecimal("250.00"), result.get(0)[3]);
    }

    @Test
    void findOrderById_ShouldHandleNullId() {
        // Given
        when(orderRepository.findById(null)).thenReturn(Optional.empty());

        // When
        Optional<Order> result = orderServiceProvider.findOrderById(null);

        // Then
        assertFalse(result.isPresent());
    }
}
