package com.tcommerce.TCommerce.domain.entities.sales;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refund {
    private String id;
    private String orderId;
    private BigInteger amount;
    private String status; // e.g., SUCCEEDED, PENDING, FAILED
    private String stripeRefundId;
    private String reason;
    private LocalDateTime createdAt;
}
