package com.tcommerce.TCommerce.infrastructure.services.mail;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.tcommerce.TCommerce.domain.events.EmailEvent;
import com.tcommerce.TCommerce.domain.services.mail.MailService;
import com.tcommerce.TCommerce.domain.exceptions.MailSendException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component("restApiMailSender")
@ConditionalOnProperty(name = "email.rest.enabled", havingValue = "true")
@Slf4j
public class RestApiMailService implements MailService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${email.rest.api-key}")
    private String apiKey;

    @Value("${email.rest.from}")
    private String fromAddress;

    @Value("${email.rest.template-id:d-584264a687334458a729dd43c4d1a6be}")
    private String templateId;

    @Override
    public void send(EmailEvent event) {
        Email from = new Email(fromAddress);
        Email to = new Email(event.getTo());
        
        Mail mail = new Mail();
        mail.setFrom(from);
        mail.setTemplateId(templateId);

        Personalization personalization = new Personalization();
        personalization.addTo(to);

        // Try to parse the event body as JSON to get dynamic fields
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> bodyMap = objectMapper.readValue(event.getBody(), Map.class);
            bodyMap.forEach(personalization::addDynamicTemplateData);
        } catch (Exception e) {
            log.warn("[SendGrid] Body is not JSON, using raw body as email_body");
            personalization.addDynamicTemplateData("email_body", event.getBody());
        }

        mail.addPersonalization(personalization);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("[SendGrid] Email sent successfully to '{}'. Status: {}", event.getTo(), response.getStatusCode());
            } else {
                log.error("[SendGrid] Failed to send email. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new MailSendException("SendGrid API returned status " + response.getStatusCode());
            }
        } catch (IOException ex) {
            log.error("[SendGrid] Error calling SendGrid API for '{}'", event.getTo(), ex);
            throw new MailSendException("Error calling SendGrid API", ex);
        }
    }
}