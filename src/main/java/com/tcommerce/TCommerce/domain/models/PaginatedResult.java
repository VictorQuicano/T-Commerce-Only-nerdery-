package com.tcommerce.TCommerce.domain.models;

import java.util.List;

public record PaginatedResult<T>(
    List<T> data,
    PageInfo pageInfo
) {}
