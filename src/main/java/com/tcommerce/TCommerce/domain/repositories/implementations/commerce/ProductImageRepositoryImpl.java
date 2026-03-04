package com.tcommerce.TCommerce.domain.repositories.implementations.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductImageRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.entities.commerce.ProductImageEntity;
import com.tcommerce.TCommerce.infrastructure.persistence.mappers.commerce.ProductImageMapper;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductImageRepository;
import com.tcommerce.TCommerce.infrastructure.persistence.repositories.commerce.JpaProductRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductImageRepositoryImpl implements ProductImageRepository {

    private final JpaProductImageRepository jpaProductImageRepository;
    private final JpaProductRepository jpaProductRepository;
    private final ProductImageMapper productImageMapper;

    public ProductImageRepositoryImpl(JpaProductImageRepository jpaProductImageRepository,
                                       JpaProductRepository jpaProductRepository,
                                       ProductImageMapper productImageMapper) {
        this.jpaProductImageRepository = jpaProductImageRepository;
        this.jpaProductRepository = jpaProductRepository;
        this.productImageMapper = productImageMapper;
    }

    @Override
    public List<ProductImage> findAll() {
        return jpaProductImageRepository.findAll().stream()
                .map(productImageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductImage> findById(String id) {
        return jpaProductImageRepository.findById(id)
                .map(productImageMapper::toDomain);
    }

    @Override
    public ProductImage save(ProductImage productImage) {
        ProductImageEntity entity = productImageMapper.toEntity(productImage);
        
        if (productImage.getProductId() != null) {
            entity.setProduct(jpaProductRepository.getReferenceById(productImage.getProductId()));
        }

        ProductImageEntity savedEntity = jpaProductImageRepository.save(entity);
        return productImageMapper.toDomain(savedEntity);
    }

    @Override
    public List<ProductImage> saveAll(List<ProductImage> images) {
        List<ProductImageEntity> entities = images.stream()
                .map(image -> {
                    ProductImageEntity entity = productImageMapper.toEntity(image);
                    if (image.getProductId() != null) {
                        entity.setProduct(jpaProductRepository.getReferenceById(image.getProductId()));
                    }
                    return entity;
                })
                .collect(Collectors.toList());

        List<ProductImageEntity> savedEntities = jpaProductImageRepository.saveAll(entities);
        return savedEntities.stream()
                .map(productImageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaProductImageRepository.deleteById(id);
    }

    @Override
    public List<ProductImage> findByProductIdOrderByDisplayOrderAsc(String productId) {
        return jpaProductImageRepository.findByProductIdOrderByDisplayOrderAsc(productId).stream()
                .map(productImageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductImage> findByImageUrl(String imageUrl) {
        return jpaProductImageRepository.findByImageUrl(imageUrl)
                .map(productImageMapper::toDomain);
    }

    @Override
    public int countByProductId(String productId) {
        return (int) jpaProductImageRepository.countByProductId(productId);
    }

    @Override
    public void delete(ProductImage image) {
        jpaProductImageRepository.deleteById(image.getId());
    }
}
