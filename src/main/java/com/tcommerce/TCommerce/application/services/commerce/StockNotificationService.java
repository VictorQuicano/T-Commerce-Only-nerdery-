package com.tcommerce.TCommerce.application.services.commerce;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.StockLevel;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.domain.services.mail.MailEventPublisher;
import com.tcommerce.TCommerce.infrastructure.services.mail.templates.MailTemplateFactory;
import com.tcommerce.TCommerce.infrastructure.services.mail.templates.MailTemplate;
import com.tcommerce.TCommerce.domain.entities.auth.User;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.auth.ERole;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockNotificationService {

    private final UserRepository userRepository;
    private final MailEventPublisher mailEventPublisher;
    private final MailTemplateFactory emailTemplateFactory;

    public void notifyUsers(Product product, StockLevel level) {
        MailTemplate template = emailTemplateFactory.stockLevelTemplate(level);

        notify(
            userRepository.findManagers(),
            product,
            template,
            ERole.MANAGER
        );

        notify(
            userRepository.findUsersWhoLikedProduct(product.getId()),
            product,
            template,
            ERole.CLIENT
        );
    }

    private void notify(
        List<User> users,
        Product product,
        MailTemplate template,
        ERole role
    ) {
        String subject = template.getSubject(product, role);
        String code = template.getCode(product, role);

        for (User user : users) {
            Map<String, String> params = template.buildParams(product, user);

            mailEventPublisher.buildAndPublish(
                user.getEmail(),
                subject,
                code,
                params
            );
        }
    }
    
    public void notifyUserNotEnoughStock(Product product, User user){
        Map<String, String> params = new HashMap<>();
        params.put("product_name", product.getName());
        params.put("category_name", product.getCategory().getName());
        params.put("price", product.getPrice().toString());
        params.put("product_id", product.getId());
        params.put("stock_quantity", product.getStock().getQuantity().toString());
        params.put("first_name", user.getFirstName());

        mailEventPublisher.buildAndPublish(
            user.getEmail(),
            "Not enough stock for product " + product.getName(),
            "not-enough-stock-client",
            params
        );
    }
}