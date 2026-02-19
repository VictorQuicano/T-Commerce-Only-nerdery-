package com.tcommerce.TCommerce.domain.services.mail;

import com.tcommerce.TCommerce.domain.events.EmailEvent;
import com.tcommerce.TCommerce.domain.exceptions.MailSendException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Service
@Slf4j
public class MailListenerService {

    private final Optional<MailService> smtpSender;
    private final Optional<MailService> restSender;
    private final Optional<MailService> mockSender;

    public MailListenerService(
            @Qualifier("smtpMailSender")    Optional<MailService> smtpSender,
            @Qualifier("restApiMailSender") Optional<MailService> restSender,
            @Qualifier("mockMailSender")    Optional<MailService> mockSender) {
        this.smtpSender = smtpSender;
        this.restSender = restSender;
        this.mockSender = mockSender;
    }

    @Async("emailTaskExecutor")
    @EventListener
    public void handleEmailEvent(EmailEvent event) {
        log.info("[EmailListener] Processing event: {}", event);

        try {
            MailService sender = resolveSender(event);
            sender.send(event);
        } catch (MailSendException ex) {
            log.error("[EmailListener] Email sending failed to '{}': {}", event.getTo(), ex.getMessage());
        } catch (Exception ex) {
            log.error("[EmailListener] Unexpected error processing event {}: {}", event, ex.getMessage());
        }
    }

    private MailService resolveSender(EmailEvent event) {
        return switch (event.getChannel()) {
            case SMTP -> smtpSender.orElseThrow(() ->
                    new MailSendException("SMTP was requested but it is not available."));
            case REST_API -> restSender.orElseThrow(() ->
                    new MailSendException("REST API was requested but it is not available."));
            case MOCK -> mockSender.orElseThrow(() ->
                    new MailSendException("Mock sender was requested but it is not available."));
            case AUTO -> getAnyAvailableSender()
                    .orElseThrow(() -> new MailSendException("No mail sender is available (SMTP, REST, or Mock)."));
        };
    }

    private Optional<MailService> getAnyAvailableSender() {
        // Preference: SMTP > REST > Mock
        if (smtpSender.isPresent()) return smtpSender;
        if (restSender.isPresent()) return restSender;
        return mockSender;
    }
}