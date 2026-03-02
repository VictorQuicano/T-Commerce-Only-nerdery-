package com.tcommerce.TCommerce.application.services.commerce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductPriceHistoryRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import com.tcommerce.TCommerce.domain.services.commerce.StockAlertService;
import com.tcommerce.TCommerce.domain.services.commerce.StockUpdater;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.CreateProductRequest;
import com.tcommerce.TCommerce.interfaces.dto.commerce.product.UpdateProductRequest;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductPriceHistory;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductImageService productImageService;
    @Mock
    private StockUpdater stockUpdater;
    @Mock
    private StockAlertService stockAlertService;
    @Mock
    private ProductPriceHistoryRepository priceHistoryRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id("cat-id")
                .name("Electronics")
                .build();

        testProduct = Product.builder()
                .id("prod-id")
                .name("Laptop")
                .price(BigInteger.valueOf(1000))
                .category(testCategory)
                .stock(new Stock(UUID.randomUUID().toString(), BigInteger.TEN, null, null))
                .images(new ArrayList<>())
                .isActive(true)
                .build();
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenExists() {
        when(productRepository.findById("prod-id")).thenReturn(Optional.of(testProduct));

        Product result = productService.getProductById("prod-id");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop");
    }

    @Test
    void createProduct_ShouldSaveProductAndPriceHistory() {
        CreateProductRequest request = new CreateProductRequest(
                "New Product", "Description", BigInteger.valueOf(500), "cat-id", BigInteger.valueOf(100));

        when(categoryRepository.findById("cat-id")).thenReturn(Optional.of(testCategory));
        when(productRepository.existsByName("New Product")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.createProduct(request);

        assertThat(result).isNotNull();
        verify(productRepository).save(any(Product.class));
        verify(priceHistoryRepository).save(any(ProductPriceHistory.class));
    }

    @Test
    void updateProduct_ShouldLogPriceHistory_WhenPriceChanges() {
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Name", null, BigInteger.valueOf(1200), true, null, null);

        when(productRepository.findById("prod-id")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.updateProduct("prod-id", request);

        verify(priceHistoryRepository).save(any(ProductPriceHistory.class));
    }

    @Test
    void deleteProduct_ShouldCallSoftDelete() {
        when(productRepository.findById("prod-id")).thenReturn(Optional.of(testProduct));

        productService.deleteProduct("prod-id");

        verify(productRepository).softDeleteById("prod-id");
    }

    @Test
    void disableProduct_ShouldSetActiveToFalse() {
        when(productRepository.findById("prod-id")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.disableProduct("prod-id");

        assertThat(testProduct.isActive()).isFalse();
        verify(productRepository).save(testProduct);
    }
}
