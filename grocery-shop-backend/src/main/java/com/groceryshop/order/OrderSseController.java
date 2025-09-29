package com.groceryshop.order;

import com.groceryshop.auth.UserRepository;
import com.groceryshop.sse.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

/**
 * SSE Controller for real-time order tracking.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order SSE", description = "Server-Sent Events for order tracking")
public class OrderSseController {

    private final SseService sseService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderSseController(SseService sseService, OrderService orderService, UserRepository userRepository) {
        this.sseService = sseService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/{orderId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Track order events", description = "Subscribe to real-time order status updates via SSE")
    public SseEmitter trackOrderEvents(@PathVariable Long orderId, Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        Order order = orderService.getOrderById(orderId);

        // Check if the user owns the order or is admin
        if (!order.getCustomer().getId().equals(userId) && !isAdmin(authentication)) {
            throw new RuntimeException("Access denied to order events");
        }

        String emitterId = "order-" + orderId + "-user-" + userId;
        SseEmitter emitter = sseService.createEmitter(emitterId);

        // Send initial order status
        sseService.sendEvent(emitterId, new com.groceryshop.sse.SseEvent(
            "order-status",
            new OrderStatusEvent(orderId, order.getStatus(), order.getUpdatedAt())
        ));

        return emitter;
    }

    /**
     * Extracts user ID from Spring Security Authentication object.
     */
    private Long getUserIdFromAuthentication(Authentication authentication) {
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
         * DTO for order status events.
         */
        public record OrderStatusEvent(Long orderId, OrderStatus status, LocalDateTime timestamp) {
    }
}
