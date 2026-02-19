package com.tcommerce.TCommerce.application.services.common;

import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import lombok.extern.slf4j.Slf4j;
import jakarta.annotation.PostConstruct;

import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ParseBodyService {

    private static final Pattern PLACEHOLDER_PATTERN =
            Pattern.compile("\\{\\{([\\w.\\-]+)\\}\\}");

    private Map<String, String> templates = new HashMap<>();

    @PostConstruct
    public void init() {
        try (InputStream inputStream = getClass().getResourceAsStream("/mail/email-bodies.yml")) {
            if (inputStream != null) {
                Yaml yaml = new Yaml();
                Map<String, Object> data = yaml.load(inputStream);
                if (data != null && data.get("templates") instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> loadedTemplates = (Map<String, String>) data.get("templates");
                    templates = loadedTemplates;
                    log.info("Successfully loaded {} email templates from email-bodies.yml", templates.size());
                }
            } else {
                log.warn("Email templates file not found: /mail/email-bodies.yml");
            }
        } catch (Exception e) {
            log.error("Failed to load email templates from YAML: {}", e.getMessage());
        }
    }

    public String buildBody(String templateName, Map<String, String> params) {
        if (templateName == null || templateName.isBlank()) {
            throw new IllegalArgumentException("Template name cannot be null or empty.");
        }

        String templateContent = templates.getOrDefault(templateName, templateName);

        if (!templates.containsKey(templateName)) {
            log.debug("Template '{}' not found in registry. Using input as raw template.", templateName);
        }

        if (params == null || params.isEmpty()) {
            return templateContent;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(templateContent);

        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = params.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public boolean hasUnresolvedPlaceholders(String resolvedBody) {
        return PLACEHOLDER_PATTERN.matcher(resolvedBody).find();
    }
}