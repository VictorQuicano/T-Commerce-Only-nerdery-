package com.tcommerce.TCommerce.infrastructure.services.mail.templates;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;

import java.util.Map;
import java.util.HashMap;

public class StockAlertTemplate implements MailTemplate {

    public String getSubject(Product product, ERole role) {

        String productName = product.getName();
        String stockQty = product.getStock() != null
                  ? product.getStock().getQuantity().toString()
                : null;

        switch (role) {

            case MANAGER:
                return String.format(
                    "URGENT: Replenishment Required for %s | Only %s Units Left",
                    productName,
                    stockQty != null ? stockQty : "Few"
                );

            case CLIENT:
                return String.format(
                    "Hurry! Only %s %s Left in Stock",
                    stockQty != null ? stockQty : "a few",
                    productName
                );

            default:
                throw new IllegalArgumentException("No subject for role " + role);
        }
    }

    public String getCode(Product product, ERole role) {
        return "stock-alert-" + role.name().toLowerCase();
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
