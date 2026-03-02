package com.tcommerce.TCommerce.application.services.auth;

import com.tcommerce.TCommerce.domain.services.mail.MailEventPublisher;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WelcomeNotificationService {

    private final MailEventPublisher mailEventPublisher;

    public void notifyUser(String email, String firstName, String lastName) {
        Map<String, String> variables = new HashMap<>();
        variables.put("first_name", firstName);
        variables.put("last_name", lastName);
        
        mailEventPublisher.buildAndPublish(
            email,
            "Welcome to TCommerce",
            "welcome",
            variables
        );
    }
}
