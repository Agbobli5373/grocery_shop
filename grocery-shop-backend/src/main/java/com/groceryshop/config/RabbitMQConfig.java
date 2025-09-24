package com.groceryshop.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * RabbitMQ configuration for the Grocery Shop event-driven architecture.
 * Defines exchanges, queues, and bindings for event messaging.
 */
@Configuration
public class RabbitMQConfig {

    // Exchange Names
    public static final String ORDER_EXCHANGE = "grocery.orders";
    public static final String INVENTORY_EXCHANGE = "grocery.inventory";
    public static final String USER_EXCHANGE = "grocery.users";
    public static final String NOTIFICATION_EXCHANGE = "grocery.notifications";

    // Queue Names
    public static final String ORDER_PROCESSING_QUEUE = "order.processing";
    public static final String ORDER_EVENTS_QUEUE = "order-events";
    public static final String INVENTORY_UPDATES_QUEUE = "inventory.updates";
    public static final String INVENTORY_EVENTS_QUEUE = "inventory-events";
    public static final String NOTIFICATION_EMAIL_QUEUE = "notification.email";
    public static final String NOTIFICATION_PUSH_QUEUE = "notification.push";
    public static final String NOTIFICATION_EVENTS_QUEUE = "notification-events";
    public static final String ANALYTICS_EVENTS_QUEUE = "analytics.events";

    // Routing Keys
    public static final String ORDER_CREATED_KEY = "order.created";
    public static final String ORDER_CONFIRMED_KEY = "order.confirmed";
    public static final String ORDER_PROCESSING_KEY = "order.processing";
    public static final String ORDER_SHIPPED_KEY = "order.shipped";
    public static final String ORDER_DELIVERED_KEY = "order.delivered";
    public static final String ORDER_CANCELLED_KEY = "order.cancelled";

    public static final String STOCK_UPDATED_KEY = "stock.updated";
    public static final String LOW_STOCK_ALERT_KEY = "stock.low";

    public static final String USER_REGISTERED_KEY = "user.registered";
    public static final String USER_LOGGED_IN_KEY = "user.logged_in";

    // Headers for notification exchange
    public static final String EMAIL_HEADER = "type";
    public static final String PUSH_HEADER = "type";
    public static final String EMAIL_VALUE = "email";
    public static final String PUSH_VALUE = "push";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Order Exchange (Direct Exchange)
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    // Inventory Exchange (Topic Exchange)
    @Bean
    public TopicExchange inventoryExchange() {
        return new TopicExchange(INVENTORY_EXCHANGE);
    }

    // User Exchange (Fanout Exchange)
    @Bean
    public FanoutExchange userExchange() {
        return new FanoutExchange(USER_EXCHANGE);
    }

    // Notification Exchange (Headers Exchange)
    @Bean
    public HeadersExchange notificationExchange() {
        return new HeadersExchange(NOTIFICATION_EXCHANGE);
    }

    // Queues
    @Bean
    public Queue orderProcessingQueue() {
        return new Queue(ORDER_PROCESSING_QUEUE, true);
    }

    @Bean
    public Queue orderEventsQueue() {
        return new Queue(ORDER_EVENTS_QUEUE, true);
    }

    @Bean
    public Queue inventoryUpdatesQueue() {
        return new Queue(INVENTORY_UPDATES_QUEUE, true);
    }

    @Bean
    public Queue inventoryEventsQueue() {
        return new Queue(INVENTORY_EVENTS_QUEUE, true);
    }

    @Bean
    public Queue notificationEmailQueue() {
        return new Queue(NOTIFICATION_EMAIL_QUEUE, true);
    }

    @Bean
    public Queue notificationPushQueue() {
        return new Queue(NOTIFICATION_PUSH_QUEUE, true);
    }

    @Bean
    public Queue notificationEventsQueue() {
        return new Queue(NOTIFICATION_EVENTS_QUEUE, true);
    }

    @Bean
    public Queue analyticsEventsQueue() {
        return new Queue(ANALYTICS_EVENTS_QUEUE, true);
    }

    // Bindings for Order Exchange
    @Bean
    public Binding orderProcessingBinding() {
        return BindingBuilder.bind(orderProcessingQueue())
                .to(orderExchange())
                .with(ORDER_PROCESSING_KEY);
    }

    // Bindings for Inventory Exchange
    @Bean
    public Binding inventoryUpdatesBinding() {
        return BindingBuilder.bind(inventoryUpdatesQueue())
                .to(inventoryExchange())
                .with(STOCK_UPDATED_KEY);
    }

    @Bean
    public Binding lowStockAlertBinding() {
        return BindingBuilder.bind(inventoryUpdatesQueue())
                .to(inventoryExchange())
                .with(LOW_STOCK_ALERT_KEY);
    }

    // Bindings for User Exchange (Fanout - no routing key needed)
    @Bean
    public Binding userAnalyticsBinding() {
        return BindingBuilder.bind(analyticsEventsQueue())
                .to(userExchange());
    }

    // Bindings for Notification Exchange (Headers)
    @Bean
    public Binding emailNotificationBinding() {
        return BindingBuilder.bind(notificationEmailQueue())
                .to(notificationExchange())
                .where(EMAIL_HEADER)
                .matches(EMAIL_VALUE);
    }

    @Bean
    public Binding pushNotificationBinding() {
        return BindingBuilder.bind(notificationPushQueue())
                .to(notificationExchange())
                .where(PUSH_HEADER)
                .matches(PUSH_VALUE);
    }

    // Bindings for SSE Event Queues
    @Bean
    public Binding orderEventsBinding() {
        return BindingBuilder.bind(orderEventsQueue())
                .to(orderExchange())
                .with("*"); // Bind to all order events
    }

    @Bean
    public Binding inventoryEventsBinding() {
        return BindingBuilder.bind(inventoryEventsQueue())
                .to(inventoryExchange())
                .with("*"); // Bind to all inventory events
    }

    @Bean
    public Binding notificationEventsEmailBinding() {
        return BindingBuilder.bind(notificationEventsQueue())
                .to(notificationExchange())
                .where(EMAIL_HEADER)
                .matches(EMAIL_VALUE);
    }

    @Bean
    public Binding notificationEventsPushBinding() {
        return BindingBuilder.bind(notificationEventsQueue())
                .to(notificationExchange())
                .where(PUSH_HEADER)
                .matches(PUSH_VALUE);
    }
}
