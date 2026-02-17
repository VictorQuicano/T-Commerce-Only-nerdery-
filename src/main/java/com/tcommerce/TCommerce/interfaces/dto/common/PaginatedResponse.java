package com.tcommerce.TCommerce.interfaces.dto.common;

import com.tcommerce.TCommerce.domain.models.PageInfo;
import java.util.List;

public record PaginatedResponse<T>(
    List<T> data,
    PageInfo pageInfo
) {}
