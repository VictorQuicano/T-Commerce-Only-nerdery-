package com.tcommerce.TCommerce.domain.repositories.interfaces.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.domain.repositories.interfaces.CRUDRepository;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CRUDRepository<Category> {
    Optional<Category> findByName(String name);
    List<Category> findByNameContaining(String name);
    boolean existsByName(String name);
}