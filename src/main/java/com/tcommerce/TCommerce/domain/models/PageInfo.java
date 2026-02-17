package com.tcommerce.TCommerce.domain.models;

public record PageInfo(
    boolean hasNextPage,
    boolean hasPreviousPage,
    String startCursor,
    String endCursor
) {}
