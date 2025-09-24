package com.groceryshop.product;

import org.springframework.context.ApplicationEvent;

public class StockUpdatedEvent extends ApplicationEvent {

    private final Long productId;
    private final String productName;
    private final Integer oldStock;
    private final Integer newStock;

    public StockUpdatedEvent(Object source, Long productId, String productName, Integer oldStock, Integer newStock) {
        super(source);
        this.productId = productId;
        this.productName = productName;
        this.oldStock = oldStock;
        this.newStock = newStock;
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getOldStock() { return oldStock; }
    public Integer getNewStock() { return newStock; }
}
