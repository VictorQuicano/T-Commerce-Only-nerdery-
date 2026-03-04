package com.tcommerce.TCommerce.domain.services.mail;

import com.tcommerce.TCommerce.application.services.common.EmailGenerator;
import com.tcommerce.TCommerce.domain.events.EmailEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailEventPublisher {
    private final ApplicationEventPublisher publisher;
    private final EmailGenerator emailGenerator;

    public void publish(String to, String subject, Map<String, Object> dynamicBody) {
        publish(to, subject, dynamicBody, EmailEvent.DeliveryChannel.AUTO);
    }

    public void publish(String to, String subject, Map<String, Object> dynamicBody, EmailEvent.DeliveryChannel channel) {
        EmailEvent event = new EmailEvent(this, to, subject, dynamicBody, channel);
        log.debug("[Publisher] Publishing EmailEvent for '{}'", to);
        publisher.publishEvent(event);
    }

    public void buildAndPublish(String to, String subject, String template,
                                Map<String, String> params){
        buildAndPublish(to, subject, template, params, EmailEvent.DeliveryChannel.AUTO);
    }

    public void buildAndPublish(String to, String subject, String template,
                                Map<String, String> params, EmailEvent.DeliveryChannel channel) {
        
        // Use EmailGenerator to create the dynamic data structure
        Map<String, Object> dynamicData = emailGenerator.generateData(template, params);
        publish(to, subject, dynamicData, channel);
    }
}