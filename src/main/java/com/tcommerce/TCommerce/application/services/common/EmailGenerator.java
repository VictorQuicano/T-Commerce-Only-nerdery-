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
                data.put("email_greeting", "Welcome to T-Commerce! 🎉");
                data.put("email_title", "We're excited to have you with us!");
                data.put("email_body", 
                    "Your account has been successfully created and you're all set to start shopping. " +
                    "Discover amazing products, great deals, and a seamless shopping experience made just for you."
                );
                data.put("button_text", "START SHOPPING 🛍️");
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
            case "order-pending":
                data.put("email_greeting", "We received your order! 🧾");
                data.put("email_title", "Your order is being reviewed");
                data.put("email_body", emailBody);
                data.put("button_text", "VIEW ORDER 👀");
                data.put("button_url", "https://t-commerce.com/orders/{{order_id}}");
                break;
            case "order-awaiting_payment":
                data.put("email_greeting", "Almost there! 💳");
                data.put("email_title", "Your order is awaiting payment");
                data.put("email_body", emailBody);
                data.put("button_text", "COMPLETE PAYMENT ✅");
                data.put("button_url", "https://t-commerce.com/orders/{{order_id}}");
                break;
            case "order-paid":
                data.put("email_greeting", "Payment confirmed! 🎉");
                data.put("email_title", "We're preparing your order");
                data.put("email_body", emailBody);
                data.put("button_text", "VIEW ORDER 📦");
                data.put("button_url", "https://t-commerce.com/orders/{{order_id}}");
                break;
            case "order-payment_failed":
                data.put("email_greeting", "Oops! Payment issue ❌");
                data.put("email_title", "We couldn't process your payment");
                data.put("email_body", emailBody);
                data.put("button_text", "RETRY PAYMENT 🔁");
                data.put("button_url", "https://t-commerce.com/orders/{{order_id}}");
                break;
            case "order-cancelled":
                data.put("email_greeting", "Order update 🚫");
                data.put("email_title", "Your order has been cancelled");
                data.put("email_body", emailBody);
                data.put("button_text", "CONTACT SUPPORT 💬");
                data.put("button_url", "https://t-commerce.com/support");
                break;
            case "order-shipped":
                data.put("email_greeting", "It's on the way! 🚚");
                data.put("email_title", "Your order has been shipped");
                data.put("email_body", emailBody);
                data.put("button_text", "TRACK YOUR ORDER 📍");
                data.put("button_url", "https://t-commerce.com/orders/{{order_id}}");
                break;
            case "order-delivered":
                data.put("email_greeting", "Success! 🎉");
                data.put("email_title", "Your order has been delivered");
                data.put("email_body", emailBody);
                data.put("button_text", "VIEW ORDER 📦");
                data.put("button_url", "https://t-commerce.com/orders/{{order_id}}");
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

