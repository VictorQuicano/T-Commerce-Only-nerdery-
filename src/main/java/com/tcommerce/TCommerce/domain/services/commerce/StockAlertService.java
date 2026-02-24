package com.tcommerce.TCommerce.domain.services.commerce;

import org.springframework.stereotype.Component;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.StockLevel;

import lombok.RequiredArgsConstructor;

import com.tcommerce.TCommerce.application.services.commerce.StockNotificationService;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class StockAlertService {

    private final StockLevelEvaluator stockLevelEvaluator;
    private final StockNotificationService stockNotificationService;

    public void processStockAlert(Product product) {

        StockLevel level = stockLevelEvaluator.evaluate(product.getStock().getQuantity());

        if (level != StockLevel.NORMAL && product.getStock().getUpdatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
            stockNotificationService.notifyUsers(product, level);
        }
    }
}