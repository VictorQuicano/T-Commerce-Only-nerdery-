package com.tcommerce.TCommerce.interfaces.dto.sales;

import java.time.LocalDateTime;
import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderHistoryResponse {
    private String id;
    private OrderStatus toStatus;
    private LocalDateTime changedAt;
    private String changedBy;
    private String reason;
}
