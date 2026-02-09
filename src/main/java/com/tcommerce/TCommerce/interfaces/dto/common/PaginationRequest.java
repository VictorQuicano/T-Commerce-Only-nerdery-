package com.tcommerce.TCommerce.interfaces.dto.common;

import java.util.Optional;

public record PaginationRequest(
        Integer first,
        Integer last,
        String after,
        String before
) {
}
