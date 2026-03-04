package com.tcommerce.TCommerce.infrastructure.services.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.tcommerce.TCommerce.domain.events.EmailEvent;
import com.tcommerce.TCommerce.domain.services.mail.MailService;
import com.tcommerce.TCommerce.application.services.common.HtmlBodyGenerator;
import org.springframework.mail.MailSendException;

@Component("smtpMailSender")
@ConditionalOnProperty(name = "email.smtp.enabled", havingValue = "true")
public class SmtpMailSender implements MailService {

    private final JavaMailSender javaMailSender;
    private final HtmlBodyGenerator htmlBodyGenerator;
    private final String fromAddress;

    public SmtpMailSender(JavaMailSender javaMailSender,
                          HtmlBodyGenerator htmlBodyGenerator,
                          @org.springframework.beans.factory.annotation.Value("${email.smtp.from}") String fromAddress) {
        this.javaMailSender = javaMailSender;
        this.htmlBodyGenerator = htmlBodyGenerator;
        this.fromAddress    = fromAddress;
    }

    @Override
    public void send(EmailEvent event) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String htmlBody = htmlBodyGenerator.generateHtml(event);

            helper.setFrom(fromAddress);
            helper.setTo(event.getTo());
            helper.setSubject(event.getSubject());
            helper.setText(htmlBody, true); // true = isHtml

            javaMailSender.send(message);

        } catch (MessagingException ex) {
            throw new MailSendException("Error at sent email SMTP to " + event.getTo(), ex);
        }
    }
}