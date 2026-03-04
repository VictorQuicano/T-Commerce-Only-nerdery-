package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record ProductStockResponse(
    BigInteger quantity,
    LocalDateTime updatedAt
) {
    
}
