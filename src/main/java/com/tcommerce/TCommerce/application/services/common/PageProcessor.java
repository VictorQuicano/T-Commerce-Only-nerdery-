package com.tcommerce.TCommerce.application.services.common;

import com.tcommerce.TCommerce.interfaces.dto.common.Pageable;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;

public abstract class PageProcessor {
    protected PaginationCriteria processRequest(Pageable request) {

        Integer first = request.first();
        Integer last = request.last();
        String after = request.after();
        String before = request.before();

        if (first != null && last != null) {
            throw new IllegalArgumentException("Cannot specify both 'first' and 'last'");
        }

        if (after != null && before != null) {
            throw new IllegalArgumentException("Cannot specify both 'after' and 'before'");
        }

        int limit;
        String cursor = after != null ? after : before;
        boolean forward = after != null;
        boolean readInReverse = false;

        if (first != null) {
            limit = first;
        } else if (last != null) {
            limit = last;
            readInReverse = true;
        } else {
            limit = 20;
        }

        limit = Math.min(limit, 100);

        return new PaginationCriteria(limit, cursor, readInReverse, forward);
    }

}
