package com.tcommerce.TCommerce.domain.events;

import org.springframework.context.ApplicationEvent;

import com.google.auto.value.AutoValue.Builder;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class EmailEvent extends ApplicationEvent {

    private final String to;
    private final String subject;
    private final String body;
    private final DeliveryChannel channel;

    public enum DeliveryChannel {
        SMTP,
        REST_API,
        MOCK,
        AUTO
    }

    public EmailEvent(Object source, String to, String subject, String body) {
        this(source, to, subject, body, DeliveryChannel.AUTO);
    }

    public EmailEvent(Object source, String to, String subject, String body, DeliveryChannel channel) {
        super(source);
        this.to      = to;
        this.subject = subject;
        this.body    = body;
        this.channel = channel;
    }


}

