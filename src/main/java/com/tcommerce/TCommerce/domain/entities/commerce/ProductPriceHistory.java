package com.tcommerce.TCommerce.domain.entities.commerce;

import lombok.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPriceHistory {
    private String id;
    private String productId;
    private BigInteger price;
    private LocalDateTime createdAt;
}
