package com.tcommerce.TCommerce.interfaces.dto.sales;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record CartItemResponse(
    String id,
    String productId,
    String productName,
    Integer quantity,
    BigInteger price,
    BigInteger subtotal,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
