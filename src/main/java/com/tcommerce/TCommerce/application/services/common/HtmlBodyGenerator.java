package com.tcommerce.TCommerce.application.services.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcommerce.TCommerce.domain.events.EmailEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class HtmlBodyGenerator {

    private final ResourceLoader resourceLoader;
    private String templateContent;

    @PostConstruct
    public void init() {
        try {
            Resource resource = resourceLoader.getResource("classpath:mail/mail-template.html");
            templateContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Error loading email template: {}", e.getMessage());
            templateContent = "<html><body>{{email_body_text}}</body></html>";
        }
    }

    public String generateHtml(EmailEvent event) {
        Map<String, Object> context = new HashMap<>();
        
        // Default values
        context.put("email_subject", event.getSubject());
        context.put("year", String.valueOf(LocalDate.now().getYear()));
        Map<String, Object> bodyMap = event.getDynamicBody();
        context.putAll(bodyMap);
        
        // Replace placeholders
        String processedHtml = templateContent;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            String value = entry.getValue() != null ? entry.getValue().toString() : "";
            processedHtml = processedHtml.replace("{{" + entry.getKey() + "}}", value);
        }

        return processedHtml;
    }
}
