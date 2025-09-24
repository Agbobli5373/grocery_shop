package com.groceryshop.cart;

import com.groceryshop.cart.spi.CartServiceProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CartServiceProvider SPI.
 * This provides access to cart functionality for other modules.
 */
@Service
public class CartServiceProviderImpl implements CartServiceProvider {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceProviderImpl(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public Optional<Cart> findCartByUserId(Long userId) {
        return cartRepository.findByCustomerId(userId);
    }

    @Override
    public List<CartItem> findCartItemsByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }
}
