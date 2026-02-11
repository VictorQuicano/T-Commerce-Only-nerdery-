package com.tcommerce.TCommerce.infrastructure.persistence.implementations;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.ProductMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.JpaProductRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.utils.CursorValue;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

import com.tcommerce.TCommerce.application.query.ProductFilter;
import com.tcommerce.TCommerce.domain.models.PageInfo;
import com.tcommerce.TCommerce.domain.models.PaginatedResult;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
    public PaginatedResult<Product> findAll(PaginationCriteria criteria, 
                                            ProductFilter filter, 
                                            String sorterBy, 
                                            String sorterDirection) {

        System.out.println("Paginations Criteria: " + criteria);
        System.out.println("Product Filter: " + filter);
        System.out.println("Sorter By: " + sorterBy);
        System.out.println("Sorter Direction: " + sorterDirection);
        System.out.println("Cursor: " + criteria.cursor());
        System.out.println("Forward: " + criteria.forward());
        System.out.println("Limit: " + criteria.limit());                                        

        Map<String, Object> parameters = new HashMap<>();
        List<String> whereConditions = new ArrayList<>();

        if (filter != null) {
            if (filter.name() != null && !filter.name().isEmpty()) {
                whereConditions.add("p.name LIKE :name");
                parameters.put("name", "%" + filter.name() + "%");
            }
            if (filter.categoryId() != null && !filter.categoryId().isEmpty()) {
                whereConditions.add("p.category.id = :categoryId");
                parameters.put("categoryId", filter.categoryId());
            }
        }

        String sortField = (sorterBy != null && !sorterBy.isBlank()) ? sorterBy : "id";
        boolean asc = sorterDirection == null || sorterDirection.equalsIgnoreCase("ASC");
        String sortFieldJpql = "p." + sortField;

        String cursor = criteria.cursor();
        boolean forward = criteria.forward();
        int limit = Math.min(criteria.limit(), 100);

        if (cursor != null && !cursor.isEmpty()) {
            CursorValue cursorValue = CursorValue.decode(cursor);
            String sortFieldValue = cursorValue.sortFieldValue();
            String lastId = cursorValue.id();

            if (forward) {
                whereConditions.add("(" + sortFieldJpql + " > :sortFieldValue OR (" +
                                    sortFieldJpql + " = :sortFieldValue AND p.id > :lastId))");
            } else {
                whereConditions.add("(" + sortFieldJpql + " < :sortFieldValue OR (" +
                                    sortFieldJpql + " = :sortFieldValue AND p.id < :lastId))");
            }
            parameters.put("sortFieldValue", convertToComparableType(sortFieldValue, sortField));
            parameters.put("lastId", lastId);
        }

        String whereClause = whereConditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", whereConditions);

        String orderBy = " ORDER BY " + sortFieldJpql + (asc ? " ASC" : " DESC") + ", p.id ASC";

        String jpql = "SELECT p FROM ProductEntity p" + whereClause + orderBy;
        TypedQuery<ProductEntity> query = entityManager.createQuery(jpql, ProductEntity.class);
        parameters.forEach(query::setParameter);

        query.setMaxResults(limit + 1);
        List<ProductEntity> entities = query.getResultList();

        boolean hasNextPage = false;
        boolean hasPreviousPage = false;

        if (forward) {
            hasNextPage = entities.size() > limit;
            if (hasNextPage) {
                entities = entities.subList(0, limit);
            }
            hasPreviousPage = cursor != null && !cursor.isEmpty();
        } else {
            hasPreviousPage = entities.size() > limit;
            if (hasPreviousPage) {
                entities = entities.subList(1, entities.size());
            }
            Collections.reverse(entities);
            hasNextPage = cursor != null && !cursor.isEmpty();
        }

        List<Product> products = entities.stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());

        String startCursor = null;
        String endCursor = null;
        if (!products.isEmpty()) {
            Product firstProduct = products.get(0);
            Product lastProduct = products.get(products.size() - 1);
            startCursor = encodeCursor(firstProduct, sortField);
            endCursor = encodeCursor(lastProduct, sortField);
        }

        PageInfo pageInfo = new PageInfo(hasNextPage, hasPreviousPage, startCursor, endCursor);

        return new PaginatedResult<>(products, pageInfo);
    }

    private Object convertToComparableType(String value, String sortField) {
        if ("price".equals(sortField)) {
            return new BigDecimal(value);
        } else if ("createdAt".equals(sortField)) {
            return LocalDateTime.parse(value);
        }
        return value;
    }

    private String encodeCursor(Product product, String sortField) {
        Object sortFieldValue = switch (sortField) {
            case "name" -> product.getName();
            case "price" -> product.getPrice().toString();
            case "createdAt" -> product.getCreatedAt().toString();
            default -> product.getId();
        };
        return new CursorValue(sortFieldValue.toString(), product.getId()).encode();
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
