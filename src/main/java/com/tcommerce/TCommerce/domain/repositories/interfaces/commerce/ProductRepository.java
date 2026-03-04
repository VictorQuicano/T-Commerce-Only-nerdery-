package com.tcommerce.TCommerce.domain.repositories.interfaces.commerce;

import com.tcommerce.TCommerce.application.query.ProductFilter;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.repositories.interfaces.CRUDRepository;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CRUDRepository<Product> {
    Optional<Product> findByName(String name);
    List<Product> findByNameContaining(String name);
    boolean existsByName(String name);
    Window<Product> findAll(ScrollPosition position, int limit, ProductFilter filter, Sort sort);
    boolean softDeleteById(String id);
    
}
