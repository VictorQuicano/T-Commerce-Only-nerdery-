package com.tcommerce.TCommerce.infrastructure.persistence.implementations;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.ProductMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.JpaProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tcommerce.TCommerce.domain.models.PageInfo;
import com.tcommerce.TCommerce.domain.models.PaginatedResult;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;

    @PersistenceContext
    private EntityManager entityManager;

    public ProductRepositoryImpl(JpaProductRepository jpaProductRepository, ProductMapper productMapper) {
        this.jpaProductRepository = jpaProductRepository;
        this.productMapper = productMapper;
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PaginatedResult<Product> findAll(PaginationCriteria criteria) {
        String jpql = "SELECT p FROM ProductEntity p";
        String cursor = criteria.cursor();
        int limit = criteria.limit();
        boolean forward = criteria.forward();

        if (cursor != null && !cursor.isEmpty()) {
            if (forward) {
                jpql += " WHERE p.id > :cursor ORDER BY p.id ASC";
            } else {
                jpql += " WHERE p.id < :cursor ORDER BY p.id DESC";
            }
        } else {
            jpql += " ORDER BY p.id ASC";
        }

        TypedQuery<ProductEntity> query = entityManager.createQuery(jpql, ProductEntity.class);
        if (cursor != null && !cursor.isEmpty()) {
            query.setParameter("cursor", cursor);
        }
        query.setMaxResults(limit + 1);

        List<ProductEntity> entities = query.getResultList();
        boolean hasNextPage = false;
        boolean hasPreviousPage = false;

        if (forward) {
             if (entities.size() > limit) {
                 hasNextPage = true;
                 entities.remove(entities.size() - 1);
             }
             if (cursor != null && !cursor.isEmpty()) {
                 hasPreviousPage = true; // Simplified assumption
             }
        } else {
             // Backward
             if (entities.size() > limit) {
                 hasPreviousPage = true;
                 entities.remove(entities.size() - 1);
             }
             if (cursor != null && !cursor.isEmpty()) {
                 hasNextPage = true; // Simplified assumption
             }
             Collections.reverse(entities);
        }

        List<Product> products = entities.stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());

        String startCursor = products.isEmpty() ? null : products.get(0).getId();
        String endCursor = products.isEmpty() ? null : products.get(products.size() - 1).getId();

        PageInfo pageInfo = new PageInfo(hasNextPage, hasPreviousPage, startCursor, endCursor);
        return new PaginatedResult<>(products, pageInfo);
    }

    @Override
    public Optional<Product> findById(String id) {
        return jpaProductRepository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = productMapper.toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(entity);
        return productMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String id) {
        jpaProductRepository.deleteById(id);
    }

    @Override
    public Optional<Product> findByName(String name) {
        return jpaProductRepository.findByName(name)
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return jpaProductRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaProductRepository.existsByName(name);
    }
}
