package com.tcommerce.TCommerce.domain.entities.sales;

import lombok.*;

import java.time.LocalDateTime;

import com.tcommerce.TCommerce.interfaces.dto.sales.OrderHistoryResponse;

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
    private String changedBy;
    private String reason;
    private LocalDateTime createdAt;

    public OrderHistoryResponse toResponse() {
        return new OrderHistoryResponse(
                this.id,
                this.toStatus,
                this.changedAt,
                this.changedBy,
                this.reason
        );
    }
}
