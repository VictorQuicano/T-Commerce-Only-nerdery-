package com.tcommerce.TCommerce.domain.exceptions;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class CrudException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;
    private final LocalDateTime timestamp;

    public CrudException(String errorCode, String message, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
    }

    public CrudException(String errorCode, String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getErrorCode() { return errorCode; }
    public HttpStatus getHttpStatus() { return httpStatus; }
    public LocalDateTime getTimestamp() { return timestamp; }
}