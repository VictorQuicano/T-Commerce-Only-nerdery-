package com.tcommerce.TCommerce.domain.entities.sales;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.interfaces.dto.sales.OrderItemResponse;
import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem implements BaseEntity {
    private String id;
    private String orderId;
    private String productId;
    private String productName;
    private Integer quantity;
    private BigInteger price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public OrderItemResponse toResponse() {
        return new OrderItemResponse(
                this.id,
                this.productId,
                this.productName,
                this.quantity,
                this.price,
                this.price.multiply(BigInteger.valueOf(this.quantity)),
                this.createdAt,
                this.updatedAt
        );
    }
}
