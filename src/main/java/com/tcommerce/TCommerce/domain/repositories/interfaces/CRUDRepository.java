package com.tcommerce.TCommerce.domain.repositories.interfaces;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;

import java.util.List;
import java.util.Optional;

public interface CRUDRepository<T extends BaseEntity>  {
    List<T> findAll();
    Optional<T> findById(String id);
    T save(T category);
    void deleteById(String id);
}