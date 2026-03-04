package com.tcommerce.TCommerce.domain.services.commerce;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockUpdater {

    private final StockAlertService stockAlertService;

    public Product update(Product product, BigInteger quantity) {

        if (product.getStock() == null) {
            throw new IllegalStateException("Product has no stock associated");
        }

        LocalDateTime now = LocalDateTime.now();
        stockAlertService.processStockAlert(product);
        product.getStock().setQuantity(quantity);
        product.getStock().setUpdatedAt(now);
        product.setUpdatedAt(now);
        return product;
    }
}