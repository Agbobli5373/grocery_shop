package com.groceryshop.cart;

import com.groceryshop.auth.UserRepository;
import com.groceryshop.order.Order;
import com.groceryshop.shared.dto.request.AddToCartRequest;
import com.groceryshop.shared.dto.request.CheckoutRequest;
import com.groceryshop.shared.dto.request.UpdateCartItemRequest;
import com.groceryshop.shared.dto.response.CartResponse;
import com.groceryshop.shared.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for cart management operations.
 */
@RestController
@RequestMapping("/api/cart")
@Tag(name = "Cart", description = "Shopping cart management endpoints")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get user's cart", description = "Retrieve the current user's shopping cart")
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Cart cart = cartService.getUserCart(userId);
        CartResponse response = mapToCartResponse(cart);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Add item to cart", description = "Add a product to the user's shopping cart")
    public ResponseEntity<CartResponse> addItemToCart(
            @RequestBody AddToCartRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Cart cart = cartService.addItemToCart(userId, request);
        CartResponse response = mapToCartResponse(cart);
        return ResponseEntity.created(null).body(response);
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Update cart item", description = "Update the quantity of a specific cart item")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Cart cart = cartService.updateCartItem(userId, itemId, request);
        CartResponse response = mapToCartResponse(cart);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Remove item from cart", description = "Remove a specific item from the user's cart")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @PathVariable Long itemId,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Cart cart = cartService.removeItemFromCart(userId, itemId);
        CartResponse response = mapToCartResponse(cart);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Clear cart", description = "Remove all items from the user's cart")
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Checkout cart", description = "Convert the cart to an order")
    public ResponseEntity<OrderResponse> checkout(
            @RequestBody CheckoutRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Order order = cartService.checkout(userId, request);
        OrderResponse response = mapToOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    /**
     * Extracts user ID from Spring Security Authentication object.
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
        // The principal is a Spring Security UserDetails object with username as email
        if (authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User userDetails) {
            String email = userDetails.getUsername();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email))
                    .getId();
        }
        throw new RuntimeException("Unable to extract user ID from authentication");
    }

    /**
     * Maps Cart entity to CartResponse DTO.
     */
    private CartResponse mapToCartResponse(Cart cart) {
        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream()
                .map(item -> new CartResponse.CartItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getUnitPrice(),
                    item.getQuantity(),
                    item.getUnitPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())),
                    item.getAddedAt()
                ))
                .collect(Collectors.toList());

        return new CartResponse(
            cart.getId(),
            cart.getCustomer().getId(),
            cart.getTotalAmount(),
            itemResponses,
            cart.getCreatedAt(),
            cart.getUpdatedAt()
        );
    }

    /**
     * Maps Order entity to OrderResponse DTO.
     */
    private OrderResponse mapToOrderResponse(Order order) {
        return getOrderResponse(order);
    }

    @NotNull
    public static OrderResponse getOrderResponse(Order order) {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
            order.getId(),
            order.getCustomer().getId(),
            order.getCustomer().getEmail(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getDeliveryAddress(),
            order.getOrderDate(),
            order.getDeliveryDate(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            itemResponses
        );
    }
}
