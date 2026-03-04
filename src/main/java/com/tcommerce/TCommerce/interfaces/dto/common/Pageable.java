package com.tcommerce.TCommerce.interfaces.dto.common;

public interface Pageable {

    Integer first();
    Integer last();
    String after();
    String before();

    default int resolveLimit() {
        return first() != null ? first() : 20;
    }
}