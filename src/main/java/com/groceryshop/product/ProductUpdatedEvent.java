package com.groceryshop.product;

import org.springframework.context.ApplicationEvent;

public class ProductUpdatedEvent extends ApplicationEvent {

    private final Long productId;
    private final String productName;

    public ProductUpdatedEvent(Object source, Long productId, String productName) {
        super(source);
        this.productId = productId;
        this.productName = productName;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
}
