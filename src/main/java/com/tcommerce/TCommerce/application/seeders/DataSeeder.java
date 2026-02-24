package com.tcommerce.TCommerce.application.seeders;

import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigInteger;    
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final Faker faker = new Faker();

    public DataSeeder(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.findAll().isEmpty()) {
            seedData();
        }
    }

    private void seedData() {
        System.out.println("Seeding data...");

        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Category category = new Category(
                    UUID.randomUUID().toString(),
                    faker.commerce().department(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            categories.add(categoryRepository.save(category));
        }

        for (int i = 0; i < 100; i++) {
            Category category = categories.get(faker.random().nextInt(categories.size()));
            LocalDateTime now = LocalDateTime.now();
            String productId = UUID.randomUUID().toString();
            BigInteger stockQuantity = BigInteger.valueOf(faker.number().numberBetween(1, 100));

            Stock stock = new Stock(
                    UUID.randomUUID().toString(),
                    stockQuantity,
                    now,
                    now
            );

            List<ProductImage> images = new ArrayList<>();
            images.add(new ProductImage(
                    UUID.randomUUID().toString(),
                    faker.internet().image(),
                    0,
                    productId,
                    now,
                    now
            ));

            Product product = new Product(
                    productId,
                    faker.commerce().productName(),
                    faker.commerce().material() + " " + faker.commerce().productName(),
                    BigInteger.valueOf(faker.number().numberBetween(10, 1000)),
                    category,
                    stock,
                    images,
                    null,
                    now,
                    now
            );

            productRepository.save(product);
        }

        System.out.println("Seeding completed successfully.");
    }
}
