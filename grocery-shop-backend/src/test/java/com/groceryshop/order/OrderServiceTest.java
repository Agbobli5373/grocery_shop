package com.groceryshop.order;

import com.groceryshop.TestDataFactory;
import com.groceryshop.auth.User;
import com.groceryshop.auth.spi.AuthServiceProvider;
import com.groceryshop.cart.Cart;
import com.groceryshop.cart.CartItem;
import com.groceryshop.cart.spi.CartServiceProvider;
import com.groceryshop.product.Product;
import com.groceryshop.product.StockUpdatedEvent;
import com.groceryshop.product.spi.ProductServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private AuthServiceProvider authServiceProvider;

    @Mock
    private CartServiceProvider cartServiceProvider;

    @Mock
    private ProductServiceProvider productServiceProvider;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private CartItem testCartItem;
    private Order testOrder;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createTestUser();
        testProduct = TestDataFactory.createTestProduct();
        testCart = TestDataFactory.createTestCart(1L, testUser);
        testCartItem = TestDataFactory.createTestCartItem(1L, testCart, testProduct);
        testOrder = TestDataFactory.createTestOrder(1L, testUser, OrderStatus.PENDING);
        testOrderItem = TestDataFactory.createTestOrderItem(1L, testOrder, testProduct);
    }

    @Test
    void createOrder_ShouldCreateOrderFromCart_WhenValidRequest() {
        // Given
        when(authServiceProvider.findUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartServiceProvider.findCartByUserId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartServiceProvider.findCartItemsByCartId(anyLong())).thenReturn(List.of(testCartItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(testOrderItem);

        // When
        Order result = orderService.createOrder(testUser.getId(), "123 Test Street");

        // Then
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(orderRepository).save(any(Order.class));
        verify(orderItemRepository).save(any(OrderItem.class));
        verify(productServiceProvider).updateProductStock(testProduct.getId(), testProduct.getStockQuantity() - testCartItem.getQuantity());
        verify(eventPublisher).publishEvent(any(StockUpdatedEvent.class));
        verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(authServiceProvider.findUserById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.createOrder(999L, "123 Test Street"));
        verify(cartServiceProvider, never()).findCartByUserId(anyLong());
    }

    @Test
    void createOrder_ShouldThrowException_WhenCartNotFound() {
        // Given
        when(authServiceProvider.findUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartServiceProvider.findCartByUserId(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.createOrder(testUser.getId(), "123 Test Street"));
        verify(cartServiceProvider, never()).findCartItemsByCartId(anyLong());
    }

    @Test
    void createOrder_ShouldThrowException_WhenCartIsEmpty() {
        // Given
        when(authServiceProvider.findUserById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartServiceProvider.findCartByUserId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartServiceProvider.findCartItemsByCartId(anyLong())).thenReturn(List.of());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.createOrder(testUser.getId(), "123 Test Street"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenOrderExists() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.getOrderById(testOrder.getId());

        // Then
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        verify(orderRepository).findById(testOrder.getId());
    }

    @Test
    void getOrderById_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.getOrderById(999L));
    }

    @Test
    void getOrdersByCustomerId_ShouldReturnOrdersList_WhenValidCustomerId() {
        // Given
        List<Order> orders = List.of(testOrder);
        when(orderRepository.findByCustomerIdOrderByOrderDateDesc(anyLong())).thenReturn(orders);

        // When
        List<Order> result = orderService.getOrdersByCustomerId(testUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOrder.getId(), result.get(0).getId());
        verify(orderRepository).findByCustomerIdOrderByOrderDateDesc(testUser.getId());
    }

    @Test
    void updateOrderStatus_ShouldUpdateStatus_WhenValidRequest() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        Order result = orderService.updateOrderStatus(testOrder.getId(), OrderStatus.CONFIRMED);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void updateOrderStatus_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () ->
            orderService.updateOrderStatus(999L, OrderStatus.CONFIRMED));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void cancelOrder_ShouldUpdateStatusToCancelled_WhenValidRequest() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

        // When
        orderService.cancelOrder(testOrder.getId());

        // Then
        assertEquals(OrderStatus.CANCELLED, testOrder.getStatus());
        verify(orderRepository).save(testOrder);
    }

    @Test
    void cancelOrder_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.cancelOrder(999L));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void trackOrder_ShouldReturnOrder_WhenOrderExists() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(testOrder));

        // When
        Order result = orderService.trackOrder(testOrder.getId());

        // Then
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        verify(orderRepository).findById(testOrder.getId());
    }

    @Test
    void trackOrder_ShouldThrowException_WhenOrderNotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> orderService.trackOrder(999L));
    }
}
