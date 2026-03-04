package com.tcommerce.TCommerce.interfaces.dto.commerce.product;

public record ProductLikeResponse(
    String productId,
    long totalLikes,
    boolean likedByMe
) {}
