package com.tcommerce.TCommerce.application.services.commerce;

import com.tcommerce.TCommerce.application.query.ProductFilter;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.domain.exceptions.AlreadyExistsException;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import com.tcommerce.TCommerce.domain.services.commerce.StockUpdater;
import com.tcommerce.TCommerce.domain.services.mail.MailEventPublisher;
import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.CreateProductRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.UpdateProductRequest;
import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.domain.entities.auth.User;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tcommerce.TCommerce.domain.services.commerce.StockAlertService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageService productImageService;
    private final StockUpdater stockUpdater;
    private final StockAlertService stockAlertService;

    public Window<Product> getAllProducts(ProductFilter filter, ScrollPosition position, int limit, Sort sort) {
        return productRepository.findAll(position, limit, filter, sort);
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
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.categoryId()));
        
        Product product = Product.builder()
                .id(productId)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .category(category)
                .stock(stock)
                .images(new ArrayList<>())
                .createdAt(now)
                .updatedAt(now)
                .build();

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
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.categoryId()));
            product.setCategory(category);
        }

        if (request.stockQuantity() != null) {
            if (product.getStock() != null) {
                product.getStock().setQuantity(request.stockQuantity());
                product.getStock().setUpdatedAt(now);
            } else {
                 product.setStock(new Stock(UUID.randomUUID().toString(), request.stockQuantity(), now, now));
            }
        }

        product.setUpdatedAt(now);
        return productRepository.save(product);
    }

    public Product reduceStock(Product product, BigInteger quantity) {
        BigInteger newStock = product.getStock().getQuantity().subtract(quantity);
        return updateStock(product, newStock);
    }
    
    @Transactional
    public Product updateStock(Product product, BigInteger quantity) {
        Product updatedProduct = stockUpdater.update(product, quantity);
        return productRepository.save(updatedProduct);
    }

    public boolean hasEnoughStock(Product product, BigInteger quantity) {
        return product.getStock().getQuantity().compareTo(quantity) >= 0;
    }

    @Transactional
    public Product addProductImages(String productId, List<org.springframework.web.multipart.MultipartFile> images) {
        Product product = getProductById(productId);
        List<ProductImage> newImages = productImageService.createProductImages(productId, images);
        product.getImages().addAll(newImages);
        return productRepository.save(product);
    }

    @Transactional
    public void removeProductImage(String productId, String imageId) {
        Product product = getProductById(productId);
        ProductImage image = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));

        productImageService.deleteProductImage(image.getImageUrl());
        product.getImages().remove(image);
        productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(String id) {
        if (!productRepository.findById(id).isPresent()) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.softDeleteById(id);
    }

    @Transactional
    public Product disableProduct(String id) {
        Product product = getProductById(id);
        product.setActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
}
