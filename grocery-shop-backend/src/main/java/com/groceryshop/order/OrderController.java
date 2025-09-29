package com.groceryshop.order;

import com.groceryshop.auth.UserRepository;
import com.groceryshop.shared.dto.response.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.groceryshop.cart.CartController.getOrderResponse;

/**
 * REST Controller for order management operations.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Get user's orders", description = "Retrieve all orders for the authenticated user")
    public ResponseEntity<List<OrderResponse>> getUserOrders(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<Order> orders = orderService.getOrdersByCustomerId(userId);
        List<OrderResponse> responses = orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Get order by ID", description = "Retrieve a specific order by its ID")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Order order = orderService.getOrderById(id);

        // Check if user owns the order or is admin
        if (!order.getCustomer().getId().equals(userId) && !isAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }

        OrderResponse response = mapToOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status", description = "Update the status of an order (Admin only)")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        Order order = orderService.updateOrderStatus(id, status);
        OrderResponse response = mapToOrderResponse(order);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Cancel order", description = "Cancel a pending order")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Order order = orderService.getOrderById(id);

        // Check if a user owns the order
        if (!order.getCustomer().getId().equals(userId)) {
            return ResponseEntity.status(403).build();
        }

        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/track")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Track order", description = "Get order tracking information")
    public ResponseEntity<OrderResponse> trackOrder(@PathVariable Long id, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Order order = orderService.trackOrder(id);

        // Check if user owns the order or is admin
        if (!order.getCustomer().getId().equals(userId) && !isAdmin(authentication)) {
            return ResponseEntity.status(403).build();
        }

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
     * Checks if the authenticated user has admin role.
     */
    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    /**
     * Maps Order entity to OrderResponse DTO.
     */
    private OrderResponse mapToOrderResponse(Order order) {
        return getOrderResponse(order);
    }
}
