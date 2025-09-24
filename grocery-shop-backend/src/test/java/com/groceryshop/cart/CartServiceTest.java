package com.groceryshop.cart;

import com.groceryshop.TestDataFactory;
import com.groceryshop.auth.User;
import com.groceryshop.auth.UserRepository;
import com.groceryshop.order.Order;
import com.groceryshop.order.OrderService;
import com.groceryshop.product.Product;
import com.groceryshop.product.ProductRepository;
import com.groceryshop.product.ProductStatus;
import com.groceryshop.shared.dto.request.AddToCartRequest;
import com.groceryshop.shared.dto.request.CheckoutRequest;
import com.groceryshop.shared.dto.request.UpdateCartItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CartServiceImpl cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private CartItem testCartItem;
    private AddToCartRequest addToCartRequest;
    private UpdateCartItemRequest updateCartItemRequest;
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        testUser = TestDataFactory.createTestUser();
        testProduct = TestDataFactory.createTestProduct();
        testCart = TestDataFactory.createTestCart(1L, testUser);
        testCartItem = TestDataFactory.createTestCartItem(1L, testCart, testProduct);
        addToCartRequest = TestDataFactory.createTestAddToCartRequest();
        updateCartItemRequest = new UpdateCartItemRequest(3);
        checkoutRequest = TestDataFactory.createTestCheckoutRequest();
    }

    @Test
    void getUserCart_ShouldReturnExistingCart_WhenCartExists() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));

        // When
        Cart result = cartService.getUserCart(testUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(testCart.getId(), result.getId());
        assertEquals(testUser.getId(), result.getCustomer().getId());
        verify(cartRepository).findByCustomerId(testUser.getId());
        verify(cartRepository, never()).save(any(Cart.class));
    }

    @Test
    void getUserCart_ShouldCreateNewCart_WhenCartDoesNotExist() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // When
        Cart result = cartService.getUserCart(testUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(testCart.getId(), result.getId());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void getUserCart_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> cartService.getUserCart(999L));
        verify(cartRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    void addItemToCart_ShouldAddNewItem_WhenItemDoesNotExist() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartItemRepository.findByCartId(anyLong())).thenReturn(List.of(testCartItem));

        // When
        Cart result = cartService.addItemToCart(testUser.getId(), addToCartRequest);

        // Then
        assertNotNull(result);
        verify(cartItemRepository).save(any(CartItem.class));
        verify(eventPublisher).publishEvent(any(ItemAddedToCartEvent.class));
    }

    @Test
    void addItemToCart_ShouldUpdateExistingItem_WhenItemAlreadyExists() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);
        when(cartItemRepository.findByCartId(anyLong())).thenReturn(List.of(testCartItem));

        // When
        Cart result = cartService.addItemToCart(testUser.getId(), addToCartRequest);

        // Then
        assertNotNull(result);
        verify(cartItemRepository).save(testCartItem);
        verify(eventPublisher).publishEvent(any(ItemAddedToCartEvent.class));
    }

    @Test
    void addItemToCart_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> cartService.addItemToCart(testUser.getId(), addToCartRequest));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_ShouldThrowException_WhenProductNotAvailable() {
        // Given
        testProduct.setStatus(ProductStatus.INACTIVE);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThrows(RuntimeException.class, () -> cartService.addItemToCart(testUser.getId(), addToCartRequest));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void addItemToCart_ShouldThrowException_WhenInsufficientStock() {
        // Given
        testProduct.setStockQuantity(1); // Less than requested quantity of 2
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThrows(RuntimeException.class, () -> cartService.addItemToCart(testUser.getId(), addToCartRequest));
        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void updateCartItem_ShouldUpdateItemQuantity_WhenValidRequest() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.findByCartId(anyLong())).thenReturn(List.of(testCartItem));

        // When
        Cart result = cartService.updateCartItem(testUser.getId(), testCartItem.getId(), updateCartItemRequest);

        // Then
        assertNotNull(result);
        verify(cartItemRepository).save(testCartItem);
    }

    @Test
    void updateCartItem_ShouldThrowException_WhenCartItemNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () ->
            cartService.updateCartItem(testUser.getId(), 999L, updateCartItemRequest));
    }

    @Test
    void updateCartItem_ShouldThrowException_WhenItemDoesNotBelongToUser() {
        // Given
        Cart otherCart = TestDataFactory.createTestCart(2L, TestDataFactory.createTestUser(2L, "other@example.com", testUser.getRole()));
        testCartItem.setCart(otherCart);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.of(testCartItem));

        // When & Then
        assertThrows(RuntimeException.class, () ->
            cartService.updateCartItem(testUser.getId(), testCartItem.getId(), updateCartItemRequest));
    }

    @Test
    void removeItemFromCart_ShouldRemoveItemAndPublishEvent_WhenValidRequest() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.findByCartId(anyLong())).thenReturn(List.of());

        // When
        Cart result = cartService.removeItemFromCart(testUser.getId(), testCartItem.getId());

        // Then
        assertNotNull(result);
        verify(cartItemRepository).delete(testCartItem);
        verify(eventPublisher).publishEvent(any(ItemRemovedFromCartEvent.class));
    }

    @Test
    void clearCart_ShouldRemoveAllItemsAndResetTotal_WhenValidRequest() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartId(anyLong())).thenReturn(List.of(testCartItem));

        // When
        cartService.clearCart(testUser.getId());

        // Then
        verify(cartItemRepository).delete(testCartItem);
        verify(cartRepository).save(testCart);
    }

    @Test
    void checkout_ShouldCreateOrderAndClearCart_WhenValidRequest() {
        // Given
        Order testOrder = TestDataFactory.createTestOrder();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartId(anyLong())).thenReturn(List.of(testCartItem));
        when(orderService.createOrder(anyLong(), anyString())).thenReturn(testOrder);

        // When
        Order result = cartService.checkout(testUser.getId(), checkoutRequest);

        // Then
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        verify(orderService).createOrder(testUser.getId(), checkoutRequest.deliveryAddress());
        verify(eventPublisher).publishEvent(any(CartCheckedOutEvent.class));
    }

    @Test
    void checkout_ShouldThrowException_WhenCartIsEmpty() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartId(anyLong())).thenReturn(List.of());

        // When & Then
        assertThrows(RuntimeException.class, () -> cartService.checkout(testUser.getId(), checkoutRequest));
        verify(orderService, never()).createOrder(anyLong(), anyString());
    }

    @Test
    void checkout_ShouldThrowException_WhenInsufficientStock() {
        // Given
        testProduct.setStockQuantity(1); // Less than cart item quantity of 2
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(cartRepository.findByCustomerId(anyLong())).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartId(anyLong())).thenReturn(List.of(testCartItem));

        // When & Then
        assertThrows(RuntimeException.class, () -> cartService.checkout(testUser.getId(), checkoutRequest));
        verify(orderService, never()).createOrder(anyLong(), anyString());
    }
}
