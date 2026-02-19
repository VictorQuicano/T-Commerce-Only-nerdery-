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

