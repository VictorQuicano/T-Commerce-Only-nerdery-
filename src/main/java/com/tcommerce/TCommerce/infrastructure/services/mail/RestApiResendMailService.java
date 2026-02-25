package com.tcommerce.TCommerce.infrastructure.services.mail;

import com.resend.*;
import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.SendEmailResponse;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Personalization;
import com.tcommerce.TCommerce.application.services.common.HtmlBodyGenerator;
import com.tcommerce.TCommerce.domain.events.EmailEvent;
import com.tcommerce.TCommerce.domain.services.mail.MailService;
import com.tcommerce.TCommerce.domain.exceptions.MailSendException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component("restApiResendMailSender")
@ConditionalOnProperty(name = "email.rest.resend.enabled", havingValue = "true")
@Slf4j
public class RestApiResendMailService implements MailService {

    @Value("${email.rest.resend.api-key}")
    private String apiKey;

    @Value("${email.rest.resend.from}")
    private String fromAddress;

    @Value("${email.rest.resend.template-id:d-584264a687334458a729dd43c4d1a6be}")
    private String templateId;

    private final HtmlBodyGenerator htmlBodyGenerator;

    public RestApiResendMailService(HtmlBodyGenerator htmlBodyGenerator) {
        this.htmlBodyGenerator = htmlBodyGenerator;
    }


    @Override
    public void send(EmailEvent event) {
        Resend resend = new Resend(apiKey);

        String htmlBody = htmlBodyGenerator.generateHtml(event);

        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from(fromAddress)
                .to(event.getTo())
                .subject(event.getSubject())
                .html(htmlBody)
                .build();

        SendEmailResponse data = resend.emails().send(sendEmailRequest);
    }
}