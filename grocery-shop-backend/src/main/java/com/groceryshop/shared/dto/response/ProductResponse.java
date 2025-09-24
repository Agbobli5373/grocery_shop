package com.groceryshop.shared.dto.response;

import com.groceryshop.product.ProductCategory;
import com.groceryshop.product.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String imageUrl,
    ProductCategory category,
    ProductStatus status,
    Integer stockQuantity,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
