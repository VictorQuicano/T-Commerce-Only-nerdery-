package com.tcommerce.TCommerce.domain.entities.commerce;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.ProductStockResponse;

import java.math.BigInteger;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock implements BaseEntity {
    private String id;
    private BigInteger quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductStockResponse toResponse() {
        return new ProductStockResponse(
                this.quantity,
                this.updatedAt
        );
    }

}
