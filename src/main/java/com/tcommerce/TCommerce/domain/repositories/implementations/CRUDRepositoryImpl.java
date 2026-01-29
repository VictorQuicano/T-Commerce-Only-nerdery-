package com.tcommerce.TCommerce.domain.repositories.implementations;

import com.tcommerce.TCommerce.domain.entities.BaseEntity;
import com.tcommerce.TCommerce.domain.repositories.interfaces.CRUDRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CRUDRepositoryImpl<T extends BaseEntity>
        implements CRUDRepository<T> {

    protected List<T> data = new ArrayList<>();

    @Override
    public List<T> findAll() {
        return data;
    }

    @Override
    public Optional<T> findById(String id) {
        return data.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    @Override
    public T save(T entity) {
        deleteById(entity.getId());
        data.add(entity);
        return entity;
    }

    @Override
    public void deleteById(String id) {
        data.removeIf(e -> e.getId().equals(id));
    }
}
