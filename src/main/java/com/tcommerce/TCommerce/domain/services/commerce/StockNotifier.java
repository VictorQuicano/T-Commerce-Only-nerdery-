package com.tcommerce.TCommerce.domain.services.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.StockLevel;

public interface StockNotifier {
    void notifyManagers(Product product, StockLevel level);
}