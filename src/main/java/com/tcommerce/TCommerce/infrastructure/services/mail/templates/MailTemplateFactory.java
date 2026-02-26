package com.tcommerce.TCommerce.infrastructure.services.mail.templates;

import org.springframework.stereotype.Component;
import com.tcommerce.TCommerce.domain.entities.commerce.StockLevel;

@Component
public class MailTemplateFactory {

    public MailTemplate stockLevelTemplate(StockLevel level) {

        return switch (level) {
            case ALERT -> new StockAlertTemplate();
            case WARNING -> new StockWarningTemplate();
            default -> throw new IllegalArgumentException("No template for NORMAL level");
        };
    }
}