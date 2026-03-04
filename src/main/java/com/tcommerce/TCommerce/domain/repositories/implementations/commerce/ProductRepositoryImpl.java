package com.tcommerce.TCommerce.domain.repositories.implementations.commerce;

import com.tcommerce.TCommerce.application.query.ProductFilter;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.commerce.ProductMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductLikeEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;


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
    public Window<Product> findAll(ScrollPosition position, int limit, ProductFilter filter, Sort sort) {
        Specification<ProductEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filter != null) {
                if (filter.name() != null && !filter.name().isEmpty()) {
                    predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.name().toLowerCase() + "%"));
                }
                if (filter.categoryId() != null && !filter.categoryId().isEmpty()) {
                    predicates.add(cb.equal(root.get("category").get("id"), filter.categoryId()));
                }
                if (filter.isActive() != null) {
                    predicates.add(cb.equal(root.get("isActive"), filter.isActive()));
                }
                if (filter.isDeleted() != null) {
                    if (filter.isDeleted()) {
                        predicates.add(cb.isNotNull(root.get("deletedAt")));
                    } else {
                        predicates.add(cb.isNull(root.get("deletedAt")));
                    }
                }
                if (filter.likedByUserId() != null && !filter.likedByUserId().isEmpty()) {
                    jakarta.persistence.criteria.Subquery<String> subquery = query.subquery(String.class);
                    jakarta.persistence.criteria.Root<ProductLikeEntity> likeRoot = subquery.from(ProductLikeEntity.class);
                    subquery.select(likeRoot.get("product").get("id"))
                        .where(cb.equal(likeRoot.get("user").get("id"), filter.likedByUserId()));
                    predicates.add(root.get("id").in(subquery));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return jpaProductRepository.findBy(spec, q -> q
                .sortBy(sort)
                .limit(limit)
                .scroll(position))
                .map(productMapper::toDomain);
    }

    @Override
    @Transactional
    public Optional<Product> findById(String id) {
        return jpaProductRepository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public boolean softDeleteById(String id) {
        int affectedRows = jpaProductRepository.softDeleteById(id, LocalDateTime.now());
        return affectedRows > 0;
    }
}