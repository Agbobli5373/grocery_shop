package com.groceryshop.notification;

import com.groceryshop.TestDataFactory;
import com.groceryshop.auth.User;
import com.groceryshop.order.Order;
import com.groceryshop.order.OrderStatus;
import com.groceryshop.product.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationServiceImpl.
 * Tests notification sending functionality including email and push notifications.
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private EmailNotificationService emailService;

    @Mock
    private PushNotificationService pushService;

    private NotificationService notificationService;
    private User testUser;
    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationServiceImpl(emailService, pushService);
        testUser = TestDataFactory.createTestUser();
        testOrder = TestDataFactory.createTestOrder();
        testOrder.setOrderDate(LocalDateTime.now()); // Set orderDate since TestDataFactory doesn't set it
        testProduct = TestDataFactory.createTestProduct();
    }

    @Test
    void sendOrderConfirmation_ShouldSendEmailAndPushNotification() {
        // When
        notificationService.sendOrderConfirmation(testOrder);

        // Then
        // Verify email notification
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq(testUser.getEmail()), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        assertTrue(emailTemplate.subject().contains("Order Confirmation"));
        assertTrue(emailTemplate.subject().contains(testOrder.getId().toString()));

        // Verify push notification
        verify(pushService).sendPushNotification(
            eq(testUser.getId().toString()),
            eq("Order Confirmed"),
            contains("has been confirmed"),
            any(Map.class)
        );
    }

    @Test
    void sendOrderStatusUpdate_ShouldSendEmailAndPushNotification_ForConfirmedStatus() {
        // Given
        testOrder.setStatus(OrderStatus.CONFIRMED);

        // When
        notificationService.sendOrderStatusUpdate(testOrder);

        // Then
        // Verify email notification
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq(testUser.getEmail()), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        assertTrue(emailTemplate.subject().contains("Order Status Update"));
        assertTrue(emailTemplate.subject().contains(testOrder.getId().toString()));

        // Verify push notification
        verify(pushService).sendPushNotification(
            eq(testUser.getId().toString()),
            eq("Order Update"),
            contains("is now confirmed"),
            any(Map.class)
        );
    }

    @Test
    void sendOrderStatusUpdate_ShouldSendEmailAndPushNotification_ForShippedStatus() {
        // Given
        testOrder.setStatus(OrderStatus.SHIPPED);

        // When
        notificationService.sendOrderStatusUpdate(testOrder);

        // Then
        // Verify email notification
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq(testUser.getEmail()), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        EmailTemplate.ResolvedEmail resolved = emailTemplate.resolve();
        assertTrue(resolved.body().contains("Your order has been shipped"));

        // Verify push notification
        verify(pushService).sendPushNotification(
            eq(testUser.getId().toString()),
            eq("Order Update"),
            contains("is now shipped"),
            any(Map.class)
        );
    }

    @Test
    void sendOrderStatusUpdate_ShouldSendEmailAndPushNotification_ForDeliveredStatus() {
        // Given
        testOrder.setStatus(OrderStatus.DELIVERED);

        // When
        notificationService.sendOrderStatusUpdate(testOrder);

        // Then
        // Verify email notification
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq(testUser.getEmail()), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        EmailTemplate.ResolvedEmail resolved = emailTemplate.resolve();
        assertTrue(resolved.body().contains("Your order has been successfully delivered"));

        // Verify push notification
        verify(pushService).sendPushNotification(
            eq(testUser.getId().toString()),
            eq("Order Update"),
            contains("is now delivered"),
            any(Map.class)
        );
    }

    @Test
    void sendOrderStatusUpdate_ShouldSendEmailAndPushNotification_ForCancelledStatus() {
        // Given
        testOrder.setStatus(OrderStatus.CANCELLED);

        // When
        notificationService.sendOrderStatusUpdate(testOrder);

        // Then
        // Verify email notification
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq(testUser.getEmail()), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        EmailTemplate.ResolvedEmail resolved = emailTemplate.resolve();
        assertTrue(resolved.body().contains("Your order has been cancelled"));

        // Verify push notification
        verify(pushService).sendPushNotification(
            eq(testUser.getId().toString()),
            eq("Order Update"),
            contains("is now cancelled"),
            any(Map.class)
        );
    }

    @Test
    void sendLowStockAlert_ShouldSendEmailToAdminAndPushNotification() {
        // When
        notificationService.sendLowStockAlert(testProduct);

        // Then
        // Verify email notification to admin
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq("admin@groceryshop.com"), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        assertTrue(emailTemplate.subject().contains("Low Stock Alert"));
        assertTrue(emailTemplate.subject().contains(testProduct.getName()));

        EmailTemplate.ResolvedEmail resolved = emailTemplate.resolve();
        assertTrue(resolved.body().contains(testProduct.getName()));
        assertTrue(resolved.body().contains("100")); // Current stock (from TestDataFactory)
        assertTrue(resolved.body().contains("10")); // Threshold (hardcoded in implementation)

        // Verify push notification to admin
        verify(pushService).sendPushNotification(
            eq("admin"),
            eq("Low Stock Alert"),
            contains("is running low on stock"),
            any(Map.class)
        );
    }

    @Test
    void sendWelcomeEmail_ShouldSendEmailAndPushNotification() {
        // When
        notificationService.sendWelcomeEmail(testUser);

        // Then
        // Verify email notification
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq(testUser.getEmail()), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        assertTrue(emailTemplate.subject().contains("Welcome to Grocery Shop"));

        EmailTemplate.ResolvedEmail resolved = emailTemplate.resolve();
        assertTrue(resolved.body().contains("Welcome " + testUser.getFirstName()));

        // Verify push notification
        verify(pushService).sendPushNotification(
            eq(testUser.getId().toString()),
            eq("Welcome!"),
            contains("Welcome to Grocery Shop"),
            any(Map.class)
        );
    }

    @Test
    void sendPasswordResetEmail_ShouldSendEmailWithResetLink() {
        // Given
        String resetToken = "reset-token-123";

        // When
        notificationService.sendPasswordResetEmail(testUser, resetToken);

        // Then
        // Verify email notification
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq(testUser.getEmail()), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        assertTrue(emailTemplate.subject().contains("Password Reset Request"));

        EmailTemplate.ResolvedEmail resolved = emailTemplate.resolve();
        assertTrue(resolved.body().contains(testUser.getFirstName()));
        assertTrue(resolved.body().contains("https://grocery-shop.com/reset-password?token=" + resetToken));

        // Verify no push notification for password reset (security)
        verify(pushService, never()).sendPushNotification(anyString(), anyString(), anyString(), any());
    }

    @Test
    void sendProductAvailableNotification_ShouldSendEmailAndPushNotification() {
        // When
        notificationService.sendProductAvailableNotification(testUser, testProduct);

        // Then
        // Verify email notification
        ArgumentCaptor<EmailTemplate> emailCaptor = ArgumentCaptor.forClass(EmailTemplate.class);
        verify(emailService).sendEmail(eq(testUser.getEmail()), emailCaptor.capture());

        EmailTemplate emailTemplate = emailCaptor.getValue();
        assertTrue(emailTemplate.subject().contains(testProduct.getName()));
        assertTrue(emailTemplate.subject().contains("is now available"));

        EmailTemplate.ResolvedEmail resolved = emailTemplate.resolve();
        assertTrue(resolved.body().contains(testUser.getFirstName()));
        assertTrue(resolved.body().contains(testProduct.getName()));
        assertTrue(resolved.body().contains(testProduct.getPrice().toString()));

        // Verify push notification
        verify(pushService).sendPushNotification(
            eq(testUser.getId().toString()),
            eq("Product Available"),
            contains("is back in stock"),
            any(Map.class)
        );
    }

    @Test
    void sendOrderConfirmation_ShouldThrowNpeForNullCustomer() {
        // Given - order with null customer (shouldn't happen in real scenario but test edge case)
        testOrder.setCustomer(null);

        // When & Then - should throw NPE as current implementation doesn't handle null customers
        assertThrows(NullPointerException.class, () -> notificationService.sendOrderConfirmation(testOrder));

        // Verify no notifications sent due to exception
        verify(emailService, never()).sendEmail(anyString(), any(EmailTemplate.class));
        verify(pushService, never()).sendPushNotification(anyString(), anyString(), anyString(), any());
    }

    @Test
    void sendOrderStatusUpdate_ShouldThrowNpeForNullCustomer() {
        // Given
        testOrder.setCustomer(null);
        testOrder.setStatus(OrderStatus.CONFIRMED);

        // When & Then - should throw NPE as current implementation doesn't handle null customers
        assertThrows(NullPointerException.class, () -> notificationService.sendOrderStatusUpdate(testOrder));

        // Verify no notifications sent due to exception
        verify(emailService, never()).sendEmail(anyString(), any(EmailTemplate.class));
        verify(pushService, never()).sendPushNotification(anyString(), anyString(), anyString(), any());
    }
}
