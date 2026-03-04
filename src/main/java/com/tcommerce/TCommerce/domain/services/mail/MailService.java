package com.tcommerce.TCommerce.domain.services.mail;

import com.tcommerce.TCommerce.domain.events.EmailEvent;

public interface MailService {
    void send(EmailEvent event);
}
