package com.groceryshop.cart;

import com.groceryshop.TestDataFactory;
import com.groceryshop.cart.spi.CartServiceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * SPI Provider Contract Test for CartServiceProvider.
 * Tests the contract implementation to ensure SPI compatibility.
 */
@ExtendWith(MockitoExtension.class)
class CartServiceProviderTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    private CartServiceProvider cartServiceProvider;
    private Cart testCart;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        cartServiceProvider = new CartServiceProviderImpl(cartRepository, cartItemRepository);
        testCart = TestDataFactory.createTestCart();
        testCartItem = TestDataFactory.createTestCartItem();
    }

    @Test
    void findCartByUserId_ShouldReturnCart_WhenCartExists() {
        // Given
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(testCart));

        // When
        Optional<Cart> result = cartServiceProvider.findCartByUserId(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCart.getId(), result.get().getId());
        assertEquals(testCart.getCustomer().getId(), result.get().getCustomer().getId());
    }

    @Test
    void findCartByUserId_ShouldReturnEmpty_WhenCartDoesNotExist() {
        // Given
        when(cartRepository.findByCustomerId(999L)).thenReturn(Optional.empty());

        // When
        Optional<Cart> result = cartServiceProvider.findCartByUserId(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findCartItemsByCartId_ShouldReturnCartItems_WhenCartItemsExist() {
        // Given
        List<CartItem> cartItems = List.of(testCartItem);
        when(cartItemRepository.findByCartId(1L)).thenReturn(cartItems);

        // When
        List<CartItem> result = cartServiceProvider.findCartItemsByCartId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCartItem.getId(), result.get(0).getId());
        assertEquals(testCartItem.getProduct().getId(), result.get(0).getProduct().getId());
    }

    @Test
    void findCartItemsByCartId_ShouldReturnEmptyList_WhenNoCartItemsExist() {
        // Given
        when(cartItemRepository.findByCartId(1L)).thenReturn(List.of());

        // When
        List<CartItem> result = cartServiceProvider.findCartItemsByCartId(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findCartByUserId_ShouldHandleNullUserId() {
        // Given
        when(cartRepository.findByCustomerId(null)).thenReturn(Optional.empty());

        // When
        Optional<Cart> result = cartServiceProvider.findCartByUserId(null);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findCartItemsByCartId_ShouldHandleNullCartId() {
        // Given
        when(cartItemRepository.findByCartId(null)).thenReturn(List.of());

        // When
        List<CartItem> result = cartServiceProvider.findCartItemsByCartId(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
