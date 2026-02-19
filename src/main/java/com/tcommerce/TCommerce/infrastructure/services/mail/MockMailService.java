package com.tcommerce.TCommerce.infrastructure.services.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;


import com.tcommerce.TCommerce.domain.services.mail.MailService;
import com.tcommerce.TCommerce.domain.events.EmailEvent;

@Component("mockMailSender")
@ConditionalOnProperty(name = "email.mock.enabled", havingValue = "true")
@Slf4j
public class MockMailService implements MailService {

    @Override
    public void send(EmailEvent event) {
        log.info("Sending email to: {}", event.getTo());
        log.info("Subject: {}", event.getSubject());
        log.info("Body: {}", event.getDynamicBody());
    }
    
}
