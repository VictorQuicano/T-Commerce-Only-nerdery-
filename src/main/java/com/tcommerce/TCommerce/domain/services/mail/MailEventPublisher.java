package com.tcommerce.TCommerce.domain.services.mail;

import com.tcommerce.TCommerce.application.services.common.EmailBodyService;
import com.tcommerce.TCommerce.application.services.common.EmailGenerator;
import com.tcommerce.TCommerce.domain.events.EmailEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class MailEventPublisher {
    private final ApplicationEventPublisher publisher;
    private final EmailBodyService emailBodyService;
    private final EmailGenerator emailGenerator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MailEventPublisher(ApplicationEventPublisher publisher, 
                            EmailBodyService emailBodyService,
                            EmailGenerator emailGenerator) {
        this.publisher = publisher;
        this.emailBodyService = emailBodyService;
        this.emailGenerator = emailGenerator;
    }

    public void publish(String to, String subject, String body) {
        publish(to, subject, body, EmailEvent.DeliveryChannel.AUTO);
    }

    public void publish(String to, String subject, String body, EmailEvent.DeliveryChannel channel) {
        EmailEvent event = new EmailEvent(this, to, subject, body, channel);
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
        
        String finalBody;
        try {
            // Convert to JSON for SendGrid/REST API (which handles dynamic templates)
            finalBody = objectMapper.writeValueAsString(dynamicData);
        } catch (Exception e) {
            log.error("[Publisher] Failed to serialize email data for '{}'", to, e);
            // Fallback to legacy string replacement if JSON fails
            finalBody = emailBodyService.buildBody(template, params);
        }

        publish(to, subject, finalBody, channel);
    }
}