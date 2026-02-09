package com.tcommerce.TCommerce.application.services.commerce;

import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.domain.exceptions.AlreadyExistsException;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.CreateProductRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.UpdateProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcommerce.TCommerce.domain.models.PaginatedResult;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;
import com.tcommerce.TCommerce.interfaces.dto.common.PaginationRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public PaginatedResult<Product> getAllProducts(PaginationRequest request) {
        int limit = 10;
        String cursor = null;
        boolean forward = true;

        if (request.first() != null) {
            limit = request.first();
            cursor = request.after();
            forward = true;
        } else if (request.last() != null) {
            limit = request.last();
            cursor = request.before();
            forward = false;
        }

        PaginationCriteria criteria = new PaginationCriteria(limit, cursor, forward);
        return productRepository.findAll(criteria);
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Transactional
    public Product createProduct(CreateProductRequest request) {
        if (productRepository.existsByName(request.name())) {
            throw new AlreadyExistsException("Product already exists with name: " + request.name(), "PRODUCT_ALREADY_EXISTS");
        }

        String productId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        Stock stock = new Stock(
                UUID.randomUUID().toString(),
                request.stockQuantity(),
                now,
                now
        );

        List<ProductImage> images = new ArrayList<>();
        if (request.imageUrls() != null) {
            for (int i = 0; i < request.imageUrls().size(); i++) {
                images.add(new ProductImage(
                        UUID.randomUUID().toString(),
                        request.imageUrls().get(i),
                        i,
                        now,
                        now
                ));
            }
        }

        Product product = new Product(
                productId,
                request.name(),
                request.description(),
                request.price(),
                request.categoryId(),
                stock,
                images,
                java.util.Optional.empty(),
                now,
                now
        );

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(String id, UpdateProductRequest request) {
        Product product = getProductById(id);
        LocalDateTime now = LocalDateTime.now();

        if (request.name() != null && !request.name().equals(product.getName())) {
            if (productRepository.existsByName(request.name())) {
                throw new AlreadyExistsException("Product already exists with name: " + request.name(), "PRODUCT_ALREADY_EXISTS");
            }
            product.setName(request.name());
        }

        if (request.description() != null) {
            product.setDescription(request.description());
        }

        if (request.price() != null) {
            product.setPrice(request.price());
        }

        if (request.categoryId() != null) {
            product.setCategoryId(request.categoryId());
        }

        if (request.stockQuantity() != null) {
            if (product.getStock() != null) {
                product.getStock().setQuantity(request.stockQuantity());
                product.getStock().setUpdatedAt(now);
            } else {
                 product.setStock(new Stock(UUID.randomUUID().toString(), request.stockQuantity(), now, now));
            }
        }

        if (request.imageUrls() != null) {
            List<ProductImage> newImages = new ArrayList<>();
            for (int i = 0; i < request.imageUrls().size(); i++) {
                 newImages.add(new ProductImage(
                        UUID.randomUUID().toString(),
                        request.imageUrls().get(i),
                        i,
                        now,
                        now
                ));
            }
            product.setImages(newImages);
        }

        product.setUpdatedAt(now);
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
