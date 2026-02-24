package com.tcommerce.TCommerce.domain.entities.sales;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusHistory {
    private String id;
    private String orderId;
    private OrderStatus fromStatus;
    private OrderStatus toStatus;
    private LocalDateTime changedAt;
    private String changedBy; // User ID
    private String reason;
    private LocalDateTime createdAt;
}
