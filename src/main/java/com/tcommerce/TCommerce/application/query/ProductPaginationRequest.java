package com.tcommerce.TCommerce.application.query;

import com.tcommerce.TCommerce.interfaces.dto.common.Pageable;

public record ProductPaginationRequest(
    String name,
    String categoryId,
    
    String cursor,
    Integer first,
    Integer last,
    String after,
    String before,
    
    String sortBy,
    String sortOrder
) implements Pageable {
}
