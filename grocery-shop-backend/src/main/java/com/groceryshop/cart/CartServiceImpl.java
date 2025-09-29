package com.groceryshop.cart;

import com.groceryshop.auth.User;
import com.groceryshop.auth.UserRepository;
import com.groceryshop.order.Order;
import com.groceryshop.order.OrderItem;
import com.groceryshop.order.OrderService;
import com.groceryshop.product.Product;
import com.groceryshop.product.ProductRepository;
import com.groceryshop.product.ProductStatus;
import com.groceryshop.shared.dto.request.AddToCartRequest;
import com.groceryshop.shared.dto.request.CheckoutRequest;
import com.groceryshop.shared.dto.request.UpdateCartItemRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CartService interface.
 */
@Service
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    public CartServiceImpl(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            OrderService orderService,
            ApplicationEventPublisher eventPublisher) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public Cart getUserCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Optional<Cart> cartOpt = cartRepository.findByCustomerId(userId);
        if (cartOpt.isPresent()) {
            return cartOpt.get();
        }

        // Create new cart if none exists
        Cart newCart = new Cart();
        newCart.setCustomer(user);
        newCart.setTotalAmount(BigDecimal.ZERO);
        return cartRepository.save(newCart);
    }

    @Override
    @Transactional
    public Cart addItemToCart(Long userId, AddToCartRequest request) {
        Cart cart = getUserCart(userId);
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + request.productId()));

        // Check if product is available
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new RuntimeException("Product is not available for purchase");
        }

        // Check stock availability
        if (product.getStockQuantity() < request.quantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        // Check if item already exists in cart
        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        CartItem cartItem;
        int oldQuantity = 0;

        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            oldQuantity = cartItem.getQuantity();
            cartItem.setQuantity(cartItem.getQuantity() + request.quantity());
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.quantity());
            cartItem.setUnitPrice(product.getPrice());
        }

        cartItem.setAddedAt(LocalDateTime.now());
        CartItem savedItem = cartItemRepository.save(cartItem);

        // Update cart total
        updateCartTotal(cart);

        // Publish event
        eventPublisher.publishEvent(new ItemAddedToCartEvent(
            this,
            cart.getId(),
            userId,
            product.getId(),
            product.getName(),
            request.quantity(),
            product.getStockQuantity() - request.quantity()
        ));

        log.info("Added {} of product {} to cart for user {}", request.quantity(), product.getName(), userId);
        return cart;
    }

    @Override
    @Transactional
    public Cart updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getUserCart(userId);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + itemId));

        // Verify item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }

        Product product = cartItem.getProduct();

        // Check stock availability for new quantity
        if (product.getStockQuantity() < request.quantity()) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStockQuantity());
        }

        cartItem.setQuantity(request.quantity());
        cartItemRepository.save(cartItem);

        // Update cart total
        updateCartTotal(cart);

        log.info("Updated cart item {} quantity to {} for user {}", itemId, request.quantity(), userId);
        return cart;
    }

    @Override
    @Transactional
    public Cart removeItemFromCart(Long userId, Long itemId) {
        Cart cart = getUserCart(userId);
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + itemId));

        // Verify item belongs to user's cart
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Cart item does not belong to user's cart");
        }

        Product product = cartItem.getProduct();
        int quantityRemoved = cartItem.getQuantity();

        cartItemRepository.delete(cartItem);

        // Update cart total
        updateCartTotal(cart);

        // Publish event
        eventPublisher.publishEvent(new ItemRemovedFromCartEvent(
            this,
            cart.getId(),
            userId,
            product.getId(),
            product.getName(),
            quantityRemoved,
            product.getStockQuantity() + quantityRemoved
        ));

        log.info("Removed item {} from cart for user {}", itemId, userId);
        return cart;
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getUserCart(userId);
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

        cartItemRepository.deleteAll(items);

        cart.setTotalAmount(BigDecimal.ZERO);
        cartRepository.save(cart);

        log.info("Cleared cart for user {}", userId);
    }

    @Override
    @Transactional
    public Order checkout(Long userId, CheckoutRequest request) {
        Cart cart = getUserCart(userId);

        // Check if the cart has items
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty()) {
            throw new RuntimeException("Cannot checkout empty cart");
        }

        // Validate stock availability for all items
        for (CartItem item : items) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
        }

        // Create an order using OrderService (this will handle stock updates and order creation)
        Order order = orderService.createOrder(userId, request.deliveryAddress());

        // Clear the cart after successful order creation
        clearCart(userId);

        // Publish checkout event
        eventPublisher.publishEvent(new CartCheckedOutEvent(
            this,
            cart.getId(),
            userId,
            order.getId(),
            order.getTotalAmount(),
            items.size()
        ));

        log.info("User {} checked out cart {} creating order {}", userId, cart.getId(), order.getId());
        return order;
    }

    /**
     * Updates the total amount of the cart based on current items.
     */
    private void updateCartTotal(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        BigDecimal total = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(total);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }
}
