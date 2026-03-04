package com.tcommerce.TCommerce.domain.exceptions;

public class NotEnoughStock extends RuntimeException{
    public NotEnoughStock(String message) {
        super(message);
    }
}
