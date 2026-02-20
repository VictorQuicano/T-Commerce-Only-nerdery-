package com.tcommerce.TCommerce.interfaces.dto.sales;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponse(
    String id,
    String productId,
    String productName,
    Integer quantity,
    BigDecimal price,
    BigDecimal subtotal,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
