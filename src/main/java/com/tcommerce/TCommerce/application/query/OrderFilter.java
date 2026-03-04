package com.tcommerce.TCommerce.application.query;

import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

public record OrderFilter(
    OrderStatus status,
    String userId
) {}
