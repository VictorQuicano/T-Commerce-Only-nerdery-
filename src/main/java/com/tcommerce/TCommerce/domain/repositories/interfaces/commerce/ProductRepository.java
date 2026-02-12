package com.tcommerce.TCommerce.domain.repositories.interfaces.commerce;

import com.tcommerce.TCommerce.application.query.ProductFilter;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.models.PaginatedResult;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import com.tcommerce.TCommerce.domain.repositories.interfaces.CRUDRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CRUDRepository<Product> {
    Optional<Product> findByName(String name);
    List<Product> findByNameContaining(String name);
    boolean existsByName(String name);
    PaginatedResult<Product> findAll(PaginationCriteria criteria, ProductFilter filter, String sorterBy, String sorterDirection );
    boolean softDeleteById(String id);
    
}
