package com.tcommerce.TCommerce.domain.models;

public record PaginationCriteria(
    Integer limit,
    String cursor,
    boolean forward
) {}
