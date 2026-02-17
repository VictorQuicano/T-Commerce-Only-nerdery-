package com.tcommerce.TCommerce.infrastructure.services.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.tcommerce.TCommerce.domain.services.EmailService;

@Service
@Slf4j
public class MockEmailService implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to: {}", to);
        log.info("Subject: {}", subject);
        log.info("Body: {}", body);
    }
}
