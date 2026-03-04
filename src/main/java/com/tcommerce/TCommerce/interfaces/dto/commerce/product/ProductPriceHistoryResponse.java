package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

import java.math.BigInteger;
import java.time.LocalDateTime;

public record ProductPriceHistoryResponse(
        String id,
        BigInteger price,
        LocalDateTime createdAt
) {}
