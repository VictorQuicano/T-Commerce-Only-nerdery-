package com.tcommerce.TCommerce.domain.exceptions;

public enum CrudErrorMessages {

    NOT_FOUND("%s with ID %s not found"),
    ALREADY_EXISTS("%s already exists with %s: %s"),
    INVALID_DATA("Invalid data for %s"),
    DUPLICATE_KEY("%s with %s: %s already exists"),
    OPERATION_FAILED("Operation failed for %s"),
    VALIDATION_ERROR("Validation error in %s"),
    UNAUTHORIZED_ACCESS("Unauthorized access for %s"),
    CONFLICT("Conflict with %s: %s");

    private final String messageTemplate;

    CrudErrorMessages(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public String format(Object... args) {
        return String.format(messageTemplate, args);
    }
}
