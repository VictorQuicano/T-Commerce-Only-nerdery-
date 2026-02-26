package com.tcommerce.TCommerce.application.query;

import com.tcommerce.TCommerce.domain.entities.sales.OrderStatus;
import com.tcommerce.TCommerce.interfaces.dto.common.Pageable;

public record OrderPaginationRequest(
    OrderStatus status,
    String userId,
    
    Integer limit,
    Integer first,
    Integer last,
    String after,
    String before,
    
    String sortBy,
    String sortOrder
) implements Pageable {
}
