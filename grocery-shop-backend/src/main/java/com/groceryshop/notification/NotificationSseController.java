package com.groceryshop.notification;

import com.groceryshop.auth.UserRepository;
import com.groceryshop.sse.SseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;

/**
 * SSE Controller for real-time user notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification SSE", description = "Server-Sent Events for user notifications")
public class NotificationSseController {

    private final SseService sseService;
    private final UserRepository userRepository;

    public NotificationSseController(SseService sseService, UserRepository userRepository) {
        this.sseService = sseService;
        this.userRepository = userRepository;
    }

    @GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    @Operation(summary = "Subscribe to notifications", description = "Receive real-time notifications via SSE")
    public SseEmitter subscribeToNotifications(Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        String emitterId = "notifications-user-" + userId;
        SseEmitter emitter = sseService.createEmitter(emitterId);

        // Send initial connection confirmation
        sseService.sendEvent(emitterId, new com.groceryshop.sse.SseEvent(
            "connection-established",
            new ConnectionEvent("Successfully connected to notification stream", java.time.LocalDateTime.now())
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
         * DTO for connection events.
         */
        public record ConnectionEvent(String message, LocalDateTime timestamp) {
    }

    /**
     * DTO for notification events.
     */
    public static class NotificationEvent {
        private final String type;
        private final String title;
        private final String message;
        private final Object data;
        private final java.time.LocalDateTime timestamp;

        public NotificationEvent(String type, String title, String message, Object data) {
            this.type = type;
            this.title = title;
            this.message = message;
            this.data = data;
            this.timestamp = java.time.LocalDateTime.now();
        }

        public String getType() {
            return type;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public Object getData() {
            return data;
        }

        public java.time.LocalDateTime getTimestamp() {
            return timestamp;
        }
    }
}
