package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import com.tcommerce.TCommerce.interfaces.dto.commerce.category.CategoryResponse;

public record ProductFullResponse(
    String id,
    String name,
    String description,
    BigInteger price,
    CategoryResponse category,
    ProductStockResponse stockQuantity,
    List<ProductImageResponse> imageUrls,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
}
