package com.groceryshop.notification;

import com.groceryshop.order.Order;
import com.groceryshop.order.OrderStatus;
import com.groceryshop.auth.User;
import com.groceryshop.product.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Implementation of the NotificationService interface.
 * Orchestrates email and push notifications for various business events.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final EmailNotificationService emailService;
    private final PushNotificationService pushService;

    public NotificationServiceImpl(
            EmailNotificationService emailService,
            PushNotificationService pushService) {
        this.emailService = emailService;
        this.pushService = pushService;
    }

    @Override
    public void sendOrderConfirmation(Order order) {
        User customer = order.getCustomer();
        String customerEmail = customer.getEmail();
        String customerId = customer.getId().toString();

        // Email notification
        EmailTemplate emailTemplate = EmailTemplate.of(
            "Order Confirmation - Order #" + order.getId(),
            """
            Dear ${customerName},

            Thank you for your order! Your order has been successfully placed.

            Order Details:
            - Order ID: ${orderId}
            - Total Amount: $${totalAmount}
            - Order Date: ${orderDate}
            - Delivery Address: ${deliveryAddress}

            You will receive updates on your order status. If you have any questions,
            please contact our customer service.

            Best regards,
            Grocery Shop Team
            """,
            Map.of(
                "customerName", customer.getFirstName() + " " + customer.getLastName(),
                "orderId", order.getId().toString(),
                "totalAmount", order.getTotalAmount().toString(),
                "orderDate", order.getOrderDate().toString(),
                "deliveryAddress", order.getDeliveryAddress()
            )
        );

        emailService.sendEmail(customerEmail, emailTemplate);

        // Push notification
        pushService.sendPushNotification(
            customerId,
            "Order Confirmed",
            "Your order #" + order.getId() + " has been confirmed",
            Map.of("orderId", order.getId(), "type", "ORDER_CONFIRMED")
        );

        log.info("Order confirmation notifications sent for order {}", order.getId());
    }

    @Override
    public void sendOrderStatusUpdate(Order order) {
        User customer = order.getCustomer();
        String customerEmail = customer.getEmail();
        String customerId = customer.getId().toString();

        String statusMessage = getStatusMessage(order.getStatus());

        // Email notification
        EmailTemplate emailTemplate = EmailTemplate.of(
            "Order Status Update - Order #" + order.getId(),
            """
            Dear ${customerName},

            Your order status has been updated.

            Order Details:
            - Order ID: ${orderId}
            - New Status: ${status}
            - Updated: ${updateTime}

            ${additionalInfo}

            Best regards,
            Grocery Shop Team
            """,
            Map.of(
                "customerName", customer.getFirstName() + " " + customer.getLastName(),
                "orderId", order.getId().toString(),
                "status", order.getStatus().toString(),
                "updateTime", order.getUpdatedAt().toString(),
                "additionalInfo", getAdditionalStatusInfo(order.getStatus())
            )
        );

        emailService.sendEmail(customerEmail, emailTemplate);

        // Push notification
        pushService.sendPushNotification(
            customerId,
            "Order Update",
            "Your order #" + order.getId() + " is now " + statusMessage.toLowerCase(),
            Map.of("orderId", order.getId(), "status", order.getStatus().toString(), "type", "ORDER_UPDATE")
        );

        log.info("Order status update notifications sent for order {}: {}", order.getId(), order.getStatus());
    }

    @Override
    public void sendLowStockAlert(Product product) {
        // This would typically send to administrators
        // For now, we'll log it and could extend to send to admin emails
        String adminEmail = "admin@groceryshop.com"; // In real app, get from config

        EmailTemplate emailTemplate = EmailTemplate.of(
            "Low Stock Alert - " + product.getName(),
            """
            Alert: Low Stock Level Detected

            Product: ${productName}
            Current Stock: ${currentStock}
            Threshold: ${threshold}

            Please restock this item soon to avoid stockouts.

            Grocery Shop System
            """,
            Map.of(
                "productName", product.getName(),
                "currentStock", product.getStockQuantity().toString(),
                "threshold", "10" // Should come from config
            )
        );

        emailService.sendEmail(adminEmail, emailTemplate);

        // Push notification to admin users (placeholder - would need admin user management)
        pushService.sendPushNotification(
            "admin", // Placeholder admin user ID
            "Low Stock Alert",
            product.getName() + " is running low on stock",
            Map.of("productId", product.getId(), "productName", product.getName(), "type", "LOW_STOCK")
        );

        log.warn("Low stock alert sent for product: {}", product.getName());
    }

    @Override
    public void sendWelcomeEmail(User user) {
        EmailTemplate emailTemplate = EmailTemplate.of(
            "Welcome to Grocery Shop!",
            """
            Welcome ${firstName}!

            Thank you for joining Grocery Shop. We're excited to have you as part of our community.

            You can now:
            - Browse our wide selection of fresh products
            - Place orders for home delivery
            - Track your orders in real-time
            - Manage your shopping cart

            Start shopping now and enjoy fresh, quality groceries delivered to your door!

            Best regards,
            Grocery Shop Team
            """,
            Map.of("firstName", user.getFirstName())
        );

        emailService.sendEmail(user.getEmail(), emailTemplate);

        // Push notification
        pushService.sendPushNotification(
            user.getId().toString(),
            "Welcome!",
            "Welcome to Grocery Shop! Start exploring our fresh products.",
            Map.of("type", "WELCOME")
        );

        log.info("Welcome notifications sent for new user: {}", user.getEmail());
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) {
        EmailTemplate emailTemplate = EmailTemplate.of(
            "Password Reset Request",
            """
            Hi ${firstName},

            You requested a password reset for your Grocery Shop account.

            Click the link below to reset your password:
            ${resetLink}

            This link will expire in 24 hours.

            If you didn't request this reset, please ignore this email.

            Best regards,
            Grocery Shop Team
            """,
            Map.of(
                "firstName", user.getFirstName(),
                "resetLink", "https://grocery-shop.com/reset-password?token=" + resetToken
            )
        );

        emailService.sendEmail(user.getEmail(), emailTemplate);

        log.info("Password reset email sent to: {}", user.getEmail());
    }

    @Override
    public void sendProductAvailableNotification(User user, Product product) {
        EmailTemplate emailTemplate = EmailTemplate.of(
            product.getName() + " is now available!",
            """
            Hi ${firstName},

            Great news! The product you were waiting for is now back in stock.

            Product: ${productName}
            Price: $${price}

            Click here to add it to your cart:
            ${productLink}

            Happy shopping!

            Best regards,
            Grocery Shop Team
            """,
            Map.of(
                "firstName", user.getFirstName(),
                "productName", product.getName(),
                "price", product.getPrice().toString(),
                "productLink", "https://grocery-shop.com/products/" + product.getId()
            )
        );

        emailService.sendEmail(user.getEmail(), emailTemplate);

        // Push notification
        pushService.sendPushNotification(
            user.getId().toString(),
            "Product Available",
            product.getName() + " is back in stock!",
            Map.of("productId", product.getId(), "type", "PRODUCT_AVAILABLE")
        );

        log.info("Product availability notification sent to {} for product: {}", user.getEmail(), product.getName());
    }

    private String getStatusMessage(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Pending";
            case CONFIRMED -> "Confirmed";
            case PROCESSING -> "Processing";
            case SHIPPED -> "Shipped";
            case DELIVERED -> "Delivered";
            case CANCELLED -> "Cancelled";
        };
    }

    private String getAdditionalStatusInfo(OrderStatus status) {
        return switch (status) {
            case SHIPPED -> "Your order has been shipped and is on its way. Track your delivery using the tracking number provided.";
            case DELIVERED -> "Your order has been successfully delivered. Thank you for shopping with us!";
            case CANCELLED -> "Your order has been cancelled. If you have any questions, please contact customer service.";
            default -> "We'll keep you updated on further progress.";
        };
    }
}
