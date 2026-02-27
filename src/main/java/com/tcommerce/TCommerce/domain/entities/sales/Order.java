package com.tcommerce.TCommerce.domain.entities.sales;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderItemResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderResponse;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderWithHistory;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderHistoryResponse;

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
    @Builder.Default
    private List<Refund> refunds = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String paymentIntentId;

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

    public OrderWithHistory toResponseWithHistory() {
        List<OrderItemResponse> itemResponses = this.items.stream()
                .map(item -> item.toResponse())
                .toList();

        List<OrderHistoryResponse> historyResponses = this.statusHistory.stream()
                .map(history -> history.toResponse())
                .toList();

        BigInteger totalAmount = itemResponses.stream()
                .map(OrderItemResponse::subtotal)
                .reduce(BigInteger.ZERO, BigInteger::add);


        return new OrderWithHistory(
                this.id,
                this.userId,
                this.status,
                itemResponses,
                historyResponses,
                totalAmount,
                this.createdAt,
                this.updatedAt
        );
    }   
        
}
