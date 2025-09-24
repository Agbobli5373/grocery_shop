package com.groceryshop.product;

import java.math.BigDecimal;

public record ProductSearchCriteria(
    String name,
    ProductCategory category,
    ProductStatus status,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    Integer page,
    Integer size,
    String sortBy,
    String sortDirection
) {
    public ProductSearchCriteria {
        if (page == null) page = 0;
        if (size == null) size = 20;
        if (sortBy == null) sortBy = "name";
        if (sortDirection == null) sortDirection = "ASC";
    }
}
