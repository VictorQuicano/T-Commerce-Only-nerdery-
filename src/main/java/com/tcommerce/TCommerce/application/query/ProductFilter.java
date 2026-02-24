package com.tcommerce.TCommerce.application.query;

public record ProductFilter(
    String name,
    String categoryId,
    Boolean isActive,
    Boolean isDeleted
) {}
