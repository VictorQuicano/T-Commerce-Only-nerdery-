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
        // 1. Construir cláusulas WHERE (filtros + cursor)
        Map<String, Object> parameters = new HashMap<>();
        List<String> whereConditions = new ArrayList<>();

        // Filtros
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

        // Determinar campo de ordenamiento y dirección
        String sortField = (sorterBy != null && !sorterBy.isBlank()) ? sorterBy : "id";
        boolean asc = sorterDirection == null || sorterDirection.equalsIgnoreCase("ASC");
        // Para JPQL, usamos alias "p." + campo
        String sortFieldJpql = "p." + sortField;

        // Procesar cursor
        String cursor = criteria.cursor();
        boolean forward = criteria.forward();
        int limit = Math.min(criteria.limit(), 100);

        if (cursor != null && !cursor.isEmpty()) {
            CursorValue cursorValue = CursorValue.decode(cursor);
            String sortFieldValue = cursorValue.sortFieldValue();
            String lastId = cursorValue.id();

            if (forward) {
                // after: (sortField > value) OR (sortField = value AND id > lastId)
                whereConditions.add("(" + sortFieldJpql + " > :sortFieldValue OR (" +
                                    sortFieldJpql + " = :sortFieldValue AND p.id > :lastId))");
            } else {
                // before: (sortField < value) OR (sortField = value AND id < lastId)
                whereConditions.add("(" + sortFieldJpql + " < :sortFieldValue OR (" +
                                    sortFieldJpql + " = :sortFieldValue AND p.id < :lastId))");
            }
            parameters.put("sortFieldValue", convertToComparableType(sortFieldValue, sortField));
            parameters.put("lastId", lastId);
        }

        String whereClause = whereConditions.isEmpty() ? "" : " WHERE " + String.join(" AND ", whereConditions);

        // 2. Construir ORDER BY
        String orderBy = " ORDER BY " + sortFieldJpql + (asc ? " ASC" : " DESC") + ", p.id ASC";
        // Para backward, el orden de los resultados debe invertirse para mantener consistencia en los cursores
        // pero la condición WHERE ya asegura que obtenemos los registros anteriores.
        // Nota: si es backward, luego invertiremos la lista para que quede en orden ascendente según el criterio.

        String jpql = "SELECT p FROM ProductEntity p" + whereClause + orderBy;
        TypedQuery<ProductEntity> query = entityManager.createQuery(jpql, ProductEntity.class);
        parameters.forEach(query::setParameter);

        // 3. Ejecutar consulta con límite + 1
        query.setMaxResults(limit + 1);
        List<ProductEntity> entities = query.getResultList();

        // 4. Determinar si hay más páginas y ajustar lista
        boolean hasNextPage = false;
        boolean hasPreviousPage = false;

        if (forward) {
            hasNextPage = entities.size() > limit;
            if (hasNextPage) {
                entities = entities.subList(0, limit);
            }
            // En paginación forward, se puede saber si hay página anterior solo si se proporcionó un cursor
            hasPreviousPage = cursor != null && !cursor.isEmpty();
        } else {
            // En backward, el primer elemento de la lista puede ser el que corresponde al cursor
            // (porque pedimos desde antes del cursor). Verificamos si hay más anteriores.
            hasPreviousPage = entities.size() > limit;
            if (hasPreviousPage) {
                // Se eliminó el primer elemento porque es el "extra" (el más cercano al cursor)
                entities = entities.subList(1, entities.size());
            }
            // Invertimos la lista para que quede en orden ascendente (consistente con forward)
            Collections.reverse(entities);
            hasNextPage = cursor != null && !cursor.isEmpty(); // si hay cursor, se puede ir adelante
        }

        // 5. Mapear a dominio
        List<Product> products = entities.stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());

        // 6. Generar cursores de inicio y fin
        String startCursor = null;
        String endCursor = null;
        if (!products.isEmpty()) {
            Product firstProduct = products.get(0);
            Product lastProduct = products.get(products.size() - 1);
            startCursor = encodeCursor(firstProduct, sortField);
            endCursor = encodeCursor(lastProduct, sortField);
        }

        // 7. Construir PageInfo (sin total/posiciones)
        PageInfo pageInfo = new PageInfo(hasNextPage, hasPreviousPage, startCursor, endCursor);

        return new PaginatedResult<>(products, pageInfo);
    }

    /**
     * Convierte el valor del cursor (String) al tipo correspondiente según el campo.
     * Ejemplo: si sortField es "price", convertir a BigDecimal; si es "createdAt", a LocalDateTime, etc.
     * Asume que el valor del cursor fue guardado en formato String; se debe adaptar a tu modelo.
     */
    private Object convertToComparableType(String value, String sortField) {
        // Implementa según los tipos de tus atributos
        // Ejemplo básico:
        if ("price".equals(sortField)) {
            return new BigDecimal(value);
        } else if ("createdAt".equals(sortField)) {
            return LocalDateTime.parse(value);
        }
        // Por defecto, tratar como String
        return value;
    }

    /**
     * Codifica un cursor a partir de una entidad Product y el campo de ordenamiento.
     */
    private String encodeCursor(Product product, String sortField) {
        Object sortFieldValue = switch (sortField) {
            case "name" -> product.getName();
            case "price" -> product.getPrice().toString();
            case "createdAt" -> product.getCreatedAt().toString();
            default -> product.getId(); // fallback: id como valor de ordenamiento
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
