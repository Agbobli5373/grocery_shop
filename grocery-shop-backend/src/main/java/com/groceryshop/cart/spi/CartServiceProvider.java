package com.groceryshop.cart.spi;

import com.groceryshop.cart.Cart;
import com.groceryshop.cart.CartItem;

import java.util.List;
import java.util.Optional;

/**
 * Service Provider Interface for cart services.
 * This interface defines the contract that other modules can use to interact with cart functionality.
 */
public interface CartServiceProvider {

    /**
     * Finds a cart by user ID.
     *
     * @param userId the user ID
     * @return Optional containing the cart if found
     */
    Optional<Cart> findCartByUserId(Long userId);

    /**
     * Finds all cart items for a given cart.
     *
     * @param cartId the cart ID
     * @return List of cart items
     */
    List<CartItem> findCartItemsByCartId(Long cartId);
}
