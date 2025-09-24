package com.groceryshop.product;

import org.springframework.context.ApplicationEvent;

public class ProductAddedEvent extends ApplicationEvent {

    private final Long productId;
    private final String productName;
    private final Integer initialStock;

    public ProductAddedEvent(Object source, Long productId, String productName, Integer initialStock) {
        super(source);
        this.productId = productId;
        this.productName = productName;
        this.initialStock = initialStock;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getInitialStock() { return initialStock; }
}
