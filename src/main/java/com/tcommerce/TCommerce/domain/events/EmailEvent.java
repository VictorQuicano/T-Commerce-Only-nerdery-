package com.tcommerce.TCommerce.domain.events;

import java.util.Map;

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
    private final Map<String, Object> dynamicBody;
    private final DeliveryChannel channel;
    private final String userId;

    public enum DeliveryChannel {
        SMTP,
        REST_API_SENDGRID,
        REST_API_RESEND,
        MOCK,
        AUTO
    }

    public EmailEvent(Object source, String to, String subject, Map<String, Object> dynamicBody) {
        this(source, to, subject, dynamicBody, DeliveryChannel.AUTO, null);
    }

    public EmailEvent(Object source, String to, String subject, Map<String, Object> dynamicBody, DeliveryChannel channel) {
        this(source, to, subject, dynamicBody, channel, null);
    }

    public EmailEvent(Object source, String to, String subject, Map<String, Object> dynamicBody, DeliveryChannel channel, String userId) {
        super(source);
        this.to             = to;
        this.subject        = subject;
        this.dynamicBody    = dynamicBody;
        this.channel        = channel;
        this.userId         = userId;
    }
}

