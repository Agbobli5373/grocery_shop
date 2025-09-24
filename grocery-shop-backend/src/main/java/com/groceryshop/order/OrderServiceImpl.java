package com.groceryshop.order;

import com.groceryshop.auth.User;
import com.groceryshop.auth.spi.AuthServiceProvider;
import com.groceryshop.cart.Cart;
import com.groceryshop.cart.CartItem;
import com.groceryshop.cart.spi.CartServiceProvider;
import com.groceryshop.product.Product;
import com.groceryshop.product.StockUpdatedEvent;
import com.groceryshop.product.spi.ProductServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the OrderService interface.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthServiceProvider authServiceProvider;
    private final CartServiceProvider cartServiceProvider;
    private final ProductServiceProvider productServiceProvider;
    private final ApplicationEventPublisher eventPublisher;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            AuthServiceProvider authServiceProvider,
            CartServiceProvider cartServiceProvider,
            ProductServiceProvider productServiceProvider,
            ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.authServiceProvider = authServiceProvider;
        this.cartServiceProvider = cartServiceProvider;
        this.productServiceProvider = productServiceProvider;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Order createOrder(Long customerId, String deliveryAddress) {
        log.info("Creating order for customer ID: {} with delivery address: {}", customerId, deliveryAddress);

        User customer = authServiceProvider.findUserById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        Cart cart = cartServiceProvider.findCartByUserId(customerId)
                .orElseThrow(() -> new RuntimeException("Cart not found for customer: " + customerId));

        List<CartItem> cartItems = cartServiceProvider.findCartItemsByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cannot create order from empty cart");
        }

        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(deliveryAddress);
        order.setTotalAmount(cart.getTotalAmount());
        order.setOrderDate(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        // Convert cart items to order items and update stock
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            orderItemRepository.save(orderItem);
            savedOrder.getItems().add(orderItem);

            // Update product stock
            Product product = cartItem.getProduct();
            int newStockQuantity = product.getStockQuantity() - cartItem.getQuantity();
            productServiceProvider.updateProductStock(product.getId(), newStockQuantity);

            // Publish stock update event
            eventPublisher.publishEvent(new StockUpdatedEvent(
                this,
                product.getId(),
                product.getName(),
                cartItem.getQuantity(),
                newStockQuantity
            ));
        }

        // Publish order created event
        eventPublisher.publishEvent(new OrderCreatedEvent(
            savedOrder.getId(),
            customerId,
            savedOrder.getTotalAmount(),
            deliveryAddress,
            savedOrder.getOrderDate(),
            customer.getEmail()
        ));

        log.info("Order created with ID: {} for customer {}", savedOrder.getId(), customerId);
        return savedOrder;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Override
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
    }

    @Override
    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = getOrderById(id);
        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);

        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated from {} to {}", id, oldStatus, status);

        return updatedOrder;
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrderById(id);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Order {} cancelled", id);
    }

    @Override
    public Order trackOrder(Long id) {
        return getOrderById(id);
    }
}
