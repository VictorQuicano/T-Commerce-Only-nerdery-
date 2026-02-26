package com.tcommerce.TCommerce.domain.services.commerce;

import org.springframework.stereotype.Component;

import java.math.BigInteger;
import com.tcommerce.TCommerce.domain.entities.commerce.StockLevel;

@Component
public class StockLevelEvaluator {
    private static final int STOCK_ALERT_THRESHOLD = 100;
    private static final int STOCK_WARNING_THRESHOLD = 200;

    public StockLevel evaluate(BigInteger quantity) {

        if (quantity.compareTo(BigInteger.valueOf(STOCK_ALERT_THRESHOLD)) < 0) {
            return StockLevel.ALERT;
        }

        if (quantity.compareTo(BigInteger.valueOf(STOCK_WARNING_THRESHOLD)) < 0) {
            return StockLevel.WARNING;
        }

        return StockLevel.NORMAL;
    }
}