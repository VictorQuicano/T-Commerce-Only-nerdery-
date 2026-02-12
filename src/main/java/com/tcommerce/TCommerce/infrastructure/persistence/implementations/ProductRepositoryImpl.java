package com.tcommerce.TCommerce.infrastructure.persistence.implementations;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.models.PageInfo;
import com.tcommerce.TCommerce.domain.models.PaginatedResult;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.ProductMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.JpaProductRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.utils.CursorValue;
import com.tcommerce.TCommerce.application.query.ProductFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
    
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

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

        String sortField = (sorterBy != null && !sorterBy.isBlank()) ? sorterBy : "id";
        boolean requestedAsc = sorterDirection == null || sorterDirection.equalsIgnoreCase("ASC");
        int limit = Math.min(criteria.limit(), 100);
        String cursor = criteria.cursor();
        boolean readInReverse = criteria.readInReverse();
        boolean forward = criteria.forward();

        // 2. Filter conditions (same as before)
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

        String sortFieldJpql = "p." + sortField;

        if (cursor == null || cursor.isEmpty()) {
            List<ProductEntity> entities;
            boolean hasNextPage;
            boolean hasPreviousPage;

            if (!readInReverse) {
                boolean effectiveSortAsc = requestedAsc;
                boolean effectiveIdAsc = true;
                String jpql = buildJpql(whereConditions, sortFieldJpql, effectiveSortAsc, effectiveIdAsc);
                TypedQuery<ProductEntity> query = entityManager.createQuery(jpql, ProductEntity.class);
                parameters.forEach(query::setParameter);
                query.setMaxResults(limit + 1);
                entities = query.getResultList();

                hasNextPage = entities.size() > limit;
                hasPreviousPage = false;
                if (hasNextPage) {
                    entities = entities.subList(0, limit);
                }
            } else {
                boolean effectiveSortAsc = !requestedAsc;
                boolean effectiveIdAsc = false; // id DESC
                String jpql = buildJpql(whereConditions, sortFieldJpql, effectiveSortAsc, effectiveIdAsc);
                TypedQuery<ProductEntity> query = entityManager.createQuery(jpql, ProductEntity.class);
                parameters.forEach(query::setParameter);
                query.setMaxResults(limit + 1);
                List<ProductEntity> revEntities = query.getResultList();

                hasPreviousPage = revEntities.size() > limit;
                hasNextPage = false;
                if (hasPreviousPage) {
                    revEntities = revEntities.subList(0, limit);
                }
                // Reverse to obtain requested order
                entities = new ArrayList<>(revEntities);
                Collections.reverse(entities);
            }

            // Convert to domain and build PageInfo
            List<Product> products = entities.stream()
                    .map(productMapper::toDomain)
                    .collect(Collectors.toList());

            String startCursor = null;
            String endCursor = null;
            if (!products.isEmpty()) {
                Product first = products.get(0);
                Product last = products.get(products.size() - 1);
                startCursor = encodeCursor(first, sortField);
                endCursor = encodeCursor(last, sortField);
            }

            PageInfo pageInfo = new PageInfo(hasNextPage, hasPreviousPage, startCursor, endCursor);
            return new PaginatedResult<>(products, pageInfo);
        }

        // 5. Case 2: Cursor exists – keyset pagination
        CursorValue cursorValue = CursorValue.decode(cursor);
        String sortFieldValueStr = cursorValue.sortFieldValue();
        String lastId = cursorValue.id();
        Object sortFieldValue = convertToComparableType(sortFieldValueStr, sortField);

        // Comparison operators based on requested sort direction and forward flag
        String sortFieldOperator;
        if (forward) {
            sortFieldOperator = requestedAsc ? ">" : "<";
        } else {
            sortFieldOperator = requestedAsc ? "<" : ">";
        }
        String idOperator = forward ? ">" : "<";

        // Add cursor condition
        whereConditions.add("(" + sortFieldJpql + " " + sortFieldOperator + " :sortFieldValue OR (" +
                            sortFieldJpql + " = :sortFieldValue AND p.id " + idOperator + " :lastId))");
        parameters.put("sortFieldValue", sortFieldValue);
        parameters.put("lastId", lastId);

        // Effective order for the query
        boolean effectiveSortAsc;
        boolean effectiveIdAsc;
        if (forward) {
            effectiveSortAsc = requestedAsc;
            effectiveIdAsc = true;
        } else {
            // Backward pagination: query in reverse order to get the immediate previous records
            effectiveSortAsc = !requestedAsc;
            effectiveIdAsc = false; // id DESC
        }

        String jpql = buildJpql(whereConditions, sortFieldJpql, effectiveSortAsc, effectiveIdAsc);
        TypedQuery<ProductEntity> query = entityManager.createQuery(jpql, ProductEntity.class);
        parameters.forEach(query::setParameter);
        query.setMaxResults(limit + 1);
        List<ProductEntity> entities = query.getResultList();

        // Process result and determine page flags
        boolean hasNextPage;
        boolean hasPreviousPage;
        if (forward) {
            hasNextPage = entities.size() > limit;
            if (hasNextPage) {
                entities = entities.subList(0, limit);
            }
            hasPreviousPage = cursor != null && !cursor.isEmpty();
        } else {
            hasPreviousPage = entities.size() > limit;
            if (hasPreviousPage) {
                entities = entities.subList(0, limit);
            }
            Collections.reverse(entities); // put back in requested order
            hasNextPage = cursor != null && !cursor.isEmpty();
        }

        List<Product> products = entities.stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());

        String startCursor = null;
        String endCursor = null;
        if (!products.isEmpty()) {
            Product first = products.get(0);
            Product last = products.get(products.size() - 1);
            startCursor = encodeCursor(first, sortField);
            endCursor = encodeCursor(last, sortField);
        }

        PageInfo pageInfo = new PageInfo(hasNextPage, hasPreviousPage, startCursor, endCursor);
        return new PaginatedResult<>(products, pageInfo);
    }

    /**
     * Builds the JPQL query string with dynamic ORDER BY.
     */
    private String buildJpql(List<String> whereConditions, String sortFieldJpql,
                             boolean sortAsc, boolean idAsc) {
        String whereClause = whereConditions.isEmpty() ? "" :
                " WHERE " + String.join(" AND ", whereConditions);
        String orderBy = " ORDER BY " + sortFieldJpql + (sortAsc ? " ASC" : " DESC") +
                ", p.id " + (idAsc ? "ASC" : "DESC");
        return "SELECT p FROM ProductEntity p" + whereClause + orderBy;
    }

    /**
     * Converts cursor string value to the appropriate Comparable type for the sort field.
     */
    private Object convertToComparableType(String value, String sortField) {
        if ("price".equals(sortField)) {
            return new BigDecimal(value);
        } else if ("createdAt".equals(sortField)) {
            return LocalDateTime.parse(value);
        }
        return value;
    }

    /**
     * Encodes a product into a cursor string.
     */
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

    @Override
    @Transactional
    public boolean softDeleteById(String id) {
        int affectedRows = jpaProductRepository.softDeleteById(id, LocalDateTime.now());
        return affectedRows > 0;
    }
}