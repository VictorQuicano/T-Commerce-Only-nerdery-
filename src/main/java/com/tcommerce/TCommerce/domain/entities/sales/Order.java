package com.tcommerce.TCommerce.domain.entities.sales;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderItemResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderResponse;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order implements BaseEntity {
    private String id;
    private String userId;
    private OrderStatus status;
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
    @Builder.Default
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public OrderResponse toResponse() {
        List<OrderItemResponse> itemResponses = this.items.stream()
                .map(item -> item.toResponse())
                .toList();

        BigInteger totalAmount = itemResponses.stream()
                .map(OrderItemResponse::subtotal)
                .reduce(BigInteger.ZERO, BigInteger::add);

        return new OrderResponse(
                this.id,
                this.userId,
                this.status,
                itemResponses,
                totalAmount,
                this.createdAt,
                this.updatedAt
        );
    }
}
