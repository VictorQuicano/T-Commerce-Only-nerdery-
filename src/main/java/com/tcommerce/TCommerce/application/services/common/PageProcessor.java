package com.tcommerce.TCommerce.application.services.common;

import com.tcommerce.TCommerce.interfaces.dto.common.Pageable;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;

public abstract class PageProcessor {
    protected PaginationCriteria processRequest(Pageable request) {

    Integer first = request.first();
    Integer last = request.last();
    String after = request.after();
    String before = request.before();

    // Validaciones de coherencia
    if (first != null && last != null) {
        throw new IllegalArgumentException("Cannot specify both 'first' and 'last'");
    }

    if (after != null && before != null) {
        throw new IllegalArgumentException("Cannot specify both 'after' and 'before'");
    }

    int limit;
    String cursor = null;
    boolean forward;

    if (first != null) {
        forward = true;
        limit = first;
        cursor = after;

        if (before != null) {
            throw new IllegalArgumentException("'before' cannot be used with 'first'");
        }

    } else if (last != null) {
        forward = false;
        limit = last;
        cursor = before;

        if (after != null) {
            throw new IllegalArgumentException("'after' cannot be used with 'last'");
        }

    } else {
        // default pagination
        forward = true;
        limit = 20;
        cursor = after; // only after makes sense in default forward
    }

    limit = Math.min(limit, 100);

    return new PaginationCriteria(limit, cursor, forward);
}

}
