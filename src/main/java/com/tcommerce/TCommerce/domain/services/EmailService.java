package com.tcommerce.TCommerce.domain.services;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
