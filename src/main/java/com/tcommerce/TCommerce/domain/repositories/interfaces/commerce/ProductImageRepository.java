package com.tcommerce.TCommerce.domain.repositories.interfaces.commerce;

import com.tcommerce.TCommerce.domain.repositories.interfaces.CRUDRepository;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends CRUDRepository<ProductImage> {
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(String productId);
    Optional<ProductImage> findByImageUrl(String imageUrl);
    int countByProductId(String productId);
    void delete(ProductImage image);
}
