package com.tcommerce.TCommerce.application.services.common;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailGenerator {

    private final ParseBodyService emailBodyService;

    public Map<String, Object> generateData(String templateName, Map<String, String> params) {
        Map<String, Object> data = new HashMap<>();
        
        String emailBody = emailBodyService.buildBody(templateName, params);

        switch (templateName) {
            case "welcome":
                data.put("email_greeting", "Welcome aboard!");
                data.put("email_title", "We're glad you're here!");
                data.put("email_body", emailBody);
                data.put("button_text", "START SHOPPING");
                data.put("button_url", "https://t-commerce.com/shop");
                break;

            case "password-reset":
                data.put("email_greeting", "Security Request");
                data.put("email_title", "Reset your password");
                data.put("email_body", emailBody);
                data.put("button_text", "RESET PASSWORD");
                data.put("button_url", params.getOrDefault("reset_url", "#"));
                break;

            case "password-changed":
                data.put("email_greeting", "Security Update");
                data.put("email_title", "Password successfully changed");
                data.put("email_body", emailBody);
                data.put("button_text", "CONTACT SUPPORT");
                data.put("button_url", "https://t-commerce.com/support");
                break;
            
            case "stock-alert-manager":
                data.put("email_greeting", "Urgent Inventory Alert");
                data.put("email_title", "Critical Stock Level Reached");
                data.put("email_body", emailBody);
                data.put("button_text", "VIEW INVENTORY");
                data.put("button_url", "https://t-commerce.com/admin/inventory");
                break;

            case "stock-alert-client":
                data.put("email_greeting", "Almost Sold Out");
                data.put("email_title", "Hurry! Limited Stock Available");
                data.put("email_body", emailBody);
                data.put("button_text", "BUY NOW");
                data.put("button_url", "https://t-commerce.com/product/{{product_id}}");
                break;

            case "stock-warning-manager":
                data.put("email_greeting", "Low Stock Warning");
                data.put("email_title", "Inventory Level Is Running Low");
                data.put("email_body", emailBody);
                data.put("button_text", "REVIEW INVENTORY");
                data.put("button_url", "https://t-commerce.com/admin/inventory");
                break;

            case "stock-warning-client":
                data.put("email_greeting", "Selling Fast");
                data.put("email_title", "Limited Stock Available");
                data.put("email_body", emailBody);
                data.put("button_text", "VIEW PRODUCT");
                data.put("button_url", "https://t-commerce.com/product/{{product_id}}");
                break;
            case "not-enough-stock-client":
                data.put("email_greeting", "Stock Update");
                data.put("email_title", "Requested Quantity Not Available");
                data.put("email_body", emailBody);
                data.put("button_text", "UPDATE CART");
                data.put("button_url", "https://t-commerce.com/cart");
                break;

            default:
                data.put("email_greeting", "Hello!");
                data.put("email_title", templateName);
                data.put("email_body", emailBody);
                data.put("button_text", "VIEW DETAILS");
                data.put("button_url", "https://t-commerce.com");
        }

        return data;
    }
}

