package com.tcommerce.TCommerce.infrastructure.persistence.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record CursorValue(String sortFieldValue, String id) {

    public String encode() {
        String raw = sortFieldValue + "::" + id;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static CursorValue decode(String cursor) {
        try {
            String decoded = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
            String[] parts = decoded.split("::", 2);
            return new CursorValue(parts[0], parts[1]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid Cursor", e);
        }
    }
}