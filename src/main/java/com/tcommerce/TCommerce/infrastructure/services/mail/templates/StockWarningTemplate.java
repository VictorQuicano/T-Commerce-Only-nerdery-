package com.tcommerce.TCommerce.infrastructure.services.mail.templates;


import java.util.Map;
import java.util.HashMap;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;
import com.tcommerce.TCommerce.domain.entities.auth.User;

public class StockWarningTemplate implements MailTemplate {
    public String getSubject(Product product, ERole role) {
        String productName = product.getName();
        String categoryName = product.getCategory() != null 
                ? product.getCategory().getName() 
                : "Uncategorized";

        String price = product.getPrice() != null 
                ? "$" + product.getPrice().toString() 
                : "N/A";

        String stockQty = product.getStock() != null 
                ? product.getStock().getQuantity().toString()
                : "Unknown";

        switch (role) {
            case MANAGER:
                return String.format(
                    "⚠ Low Stock Alert: %s | Category: %s | Current Stock: %s",
                    productName,
                    categoryName,
                    stockQty != null ? stockQty : "Unknown"
                );

            case CLIENT:
                return String.format(
                    "Update on %s – Limited Availability at %s",
                    productName,
                    price
                );

            default:
                throw new IllegalArgumentException("No subject for role " + role);
        }
    }

    public String getCode(Product product, ERole role) {
        return "stock-warning-" + role.name().toLowerCase();
    }

    public Map<String, String> buildParams(Product product, User user) {

        Map<String, String> params = new HashMap<>();
        params.put("product_name", product.getName());
        params.put("category_name", product.getCategory().getName());
        params.put("price", product.getPrice().toString());
        params.put("product_id", product.getId());
        params.put("stock_quantity", product.getStock().getQuantity().toString());
        params.put("first_name", user.getFirstName());
        params.put("last_name", user.getLastName());
        return params;
    }
}