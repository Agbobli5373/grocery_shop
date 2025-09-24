package com.groceryshop.cart;

import com.groceryshop.order.Order;
import com.groceryshop.shared.dto.request.AddToCartRequest;
import com.groceryshop.shared.dto.request.CheckoutRequest;
import com.groceryshop.shared.dto.request.UpdateCartItemRequest;

/**
 * Service interface for cart management operations.
 */
public interface CartService {

    /**
     * Gets the user's cart, creating one if it doesn't exist.
     */
    Cart getUserCart(Long userId);

    /**
     * Adds an item to the user's cart.
     */
    Cart addItemToCart(Long userId, AddToCartRequest request);

    /**
     * Updates the quantity of a cart item.
     */
    Cart updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);

    /**
     * Removes an item from the user's cart.
     */
    Cart removeItemFromCart(Long userId, Long itemId);

    /**
     * Clears all items from the user's cart.
     */
    void clearCart(Long userId);

    /**
     * Converts the cart to an order (checkout).
     */
    Order checkout(Long userId, CheckoutRequest request);
}
