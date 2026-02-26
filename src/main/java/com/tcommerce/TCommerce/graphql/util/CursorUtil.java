package com.tcommerce.TCommerce.graphql.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Utility for encoding/decoding Relay-style cursors.
 * Cursor format: Base64("createdAt::id")
 */
public class CursorUtil {

    private static final String SEPARATOR = "::";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private CursorUtil() {}

    public static String encode(LocalDateTime createdAt, String id) {
        String raw = FORMATTER.format(createdAt) + SEPARATOR + id;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static DecodedCursor decode(String cursor) {
        String raw = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
        String[] parts = raw.split(SEPARATOR, 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid cursor format");
        }
        LocalDateTime createdAt = LocalDateTime.parse(parts[0], FORMATTER);
        String id = parts[1];
        return new DecodedCursor(createdAt, id);
    }

    public record DecodedCursor(LocalDateTime createdAt, String id) {}
}
