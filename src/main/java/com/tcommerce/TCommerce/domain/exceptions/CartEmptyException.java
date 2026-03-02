package com.tcommerce.TCommerce.domain.exceptions;


public class CartEmptyException extends RuntimeException {
    public CartEmptyException(String message) {
        super(message);
    }
}