package com.groceryshop.notification;

import com.groceryshop.order.Order;
import com.groceryshop.auth.User;
import com.groceryshop.product.Product;

/**
 * Service interface for handling various types of notifications.
 */
public interface NotificationService {

    /**
     * Sends order confirmation notification to customer.
     *
     * @param order the order that was created
     */
    void sendOrderConfirmation(Order order);

    /**
     * Sends order status update notification to customer.
     *
     * @param order the order with updated status
     */
    void sendOrderStatusUpdate(Order order);

    /**
     * Sends low stock alert to administrators.
     *
     * @param product the product with low stock
     */
    void sendLowStockAlert(Product product);

    /**
     * Sends welcome email to new user.
     *
     * @param user the newly registered user
     */
    void sendWelcomeEmail(User user);

    /**
     * Sends password reset notification.
     *
     * @param user the user requesting password reset
     * @param resetToken the password reset token
     */
    void sendPasswordResetEmail(User user, String resetToken);

    /**
     * Sends product availability notification.
     *
     * @param user the user to notify
     * @param product the product that is now available
     */
    void sendProductAvailableNotification(User user, Product product);
}
