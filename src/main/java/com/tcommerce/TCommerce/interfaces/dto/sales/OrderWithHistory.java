package com.tcommerce.TCommerce.interfaces.dto.sales;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderWithHistory {
    private String id;
    private String userId;
    private OrderStatus status;
    private List<OrderItemResponse> items;
    private List<OrderHistoryResponse> history;
    private BigInteger totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
