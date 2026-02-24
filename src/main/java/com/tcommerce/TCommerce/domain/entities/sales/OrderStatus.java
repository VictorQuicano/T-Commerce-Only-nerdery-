package com.tcommerce.TCommerce.domain.entities.sales;

public enum OrderStatus {
    PENDING,
    AWAITING_PAYMENT,
    PAID,
    PAYMENT_FAILED,
    CANCELLED,
    SHIPPED,
    DELIVERED
}
