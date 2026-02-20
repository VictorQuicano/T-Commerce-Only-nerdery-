package com.tcommerce.TCommerce.interfaces.dto.sales;

import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    String id,
    String userId,
    OrderStatus status,
    List<OrderItemResponse> items,
    BigInteger totalAmount,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
