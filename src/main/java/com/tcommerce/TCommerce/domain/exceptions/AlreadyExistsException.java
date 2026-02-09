package com.tcommerce.TCommerce.domain.exceptions;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends CrudException {

    public AlreadyExistsException(String message, String errorCode) {
        super(errorCode, message, HttpStatus.CONFLICT);
    }
}
