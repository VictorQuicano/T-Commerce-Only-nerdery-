package com.tcommerce.TCommerce.application.seeders;

import com.tcommerce.TCommerce.domain.entities.commerce.Category;
import com.tcommerce.TCommerce.domain.entities.commerce.Product;
import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.entities.commerce.Stock;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.CategoryRepository;
import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductRepository;

import lombok.RequiredArgsConstructor;

import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductImageRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigInteger;    
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final Faker faker = new Faker();

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
            try{
                Category category = new Category(
                        UUID.randomUUID().toString(),
                        faker.commerce().department(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
                categories.add(categoryRepository.save(category));
            }catch (Exception e){
                System.out.println("Error seeding category: " + e.getMessage());
            }
        }

        for (int i = 0; i < 100; i++) {
            Category category = categories.get(faker.random().nextInt(categories.size()));
            LocalDateTime now = LocalDateTime.now();
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
                    "https://photos.fife.usercontent.google.com/pw/AP1GczNDFaLvUQpV3Ne5_2HNGEo7sTwAMFys8Uf-X5cTbctMDRseiRk7gdeMHA=w2200-h1238-s-no-gm?authuser=0",
                    0,
                    null,
                    now,
                    now
            ));

            Product product = Product.builder()
                    .name(faker.commerce().productName())
                    .description(faker.commerce().material() + " " + faker.commerce().productName())
                    .price(BigInteger.valueOf(faker.number().numberBetween(10, 1000)))
                    .category(category)
                    .stock(stock)
                    .images(images)
                    .createdAt(now)
                    .updatedAt(now)
                    .build(); 
            
            Product savedProduct = productRepository.save(product);

            images.forEach(image -> image.setProductId(savedProduct.getId()));
            productImageRepository.saveAll(images);
        }

        System.out.println("Seeding completed successfully.");
    }
}
