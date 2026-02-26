package com.tcommerce.TCommerce.application.services.commerce;

import com.tcommerce.TCommerce.application.query.ProductPaginationRequest;
import com.tcommerce.TCommerce.application.query.ProductFilter;
import com.tcommerce.TCommerce.application.services.common.PageProcessor;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.domain.exceptions.AlreadyExistsException;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.CreateProductRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.UpdateProductRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcommerce.TCommerce.domain.models.PaginatedResult;
import com.tcommerce.TCommerce.domain.models.PaginationCriteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService extends PageProcessor{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageService productImageService; 

    public ProductService(ProductRepository productRepository, 
                          CategoryRepository categoryRepository,
                          ProductImageService productImageService) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productImageService = productImageService;
    }   

    public PaginatedResult<Product> getAllProducts(ProductPaginationRequest request) {
        PaginationCriteria criteria = processRequest(request);
        ProductFilter filter = new ProductFilter(request.name(), request.categoryId());
        return productRepository.findAll(criteria, filter, request.sortBy(), request.sortOrder());
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
                //.id(productId)
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

        if (request.images() != null && !request.images().isEmpty()) {
            List<ProductImage> newImages = productImageService.createProductImages(id, request.images());
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
        productRepository.softDeleteById(id);
    }
}
