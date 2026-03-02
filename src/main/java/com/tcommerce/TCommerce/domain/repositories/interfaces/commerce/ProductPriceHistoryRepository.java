package com.tcommerce.TCommerce.domain.repositories.interfaces.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.ProductPriceHistory;
import java.util.List;

public interface ProductPriceHistoryRepository {
    ProductPriceHistory save(ProductPriceHistory history);
    List<ProductPriceHistory> findByProductId(String productId);
}
