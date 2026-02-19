package com.tcommerce.TCommerce.application.query;

import org.springframework.web.bind.annotation.RequestParam;

import com.tcommerce.TCommerce.interfaces.dto.common.Pageable;

public record ProductPaginationRequest(
    String name,
    String categoryId,
    
    Integer limit,
    Integer first,
    Integer last,
    String after,
    String before,
    
    String sortBy,
    String sortOrder
) implements Pageable {
}
