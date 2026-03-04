package com.tcommerce.TCommerce.application.services.commerce;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.tcommerce.TCommerce.domain.entities.commerce.ProductImage;
import com.tcommerce.TCommerce.domain.services.StorageService;

import lombok.RequiredArgsConstructor;

import com.tcommerce.TCommerce.domain.repositories.interfaces.commerce.ProductImageRepository;
import java.util.ArrayList;

import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageService {

    private final StorageService storageService;
    private final ProductImageRepository productImageRepository;

    public List<ProductImage> createProductImages(String productId, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }

        List<ProductImage> images = new ArrayList<>();
        int displayOrder = productImageRepository.countByProductId(productId) + 1;
        for (MultipartFile file : files) {
            String imageUrl = storageService.uploadImage(file, productId);

            ProductImage productImage = new ProductImage(productId, imageUrl, displayOrder++);

            productImage = productImageRepository.save(productImage);

            images.add(productImage);
        }

        return images;
    }

    public void deleteProductImage(String imageUrl) {
        ProductImage image = productImageRepository.findByImageUrl(imageUrl)
                .orElseThrow(() -> new RuntimeException("Product Image not found with imageUrl: " + imageUrl));

        storageService.deleteFile(imageUrl);

        productImageRepository.delete(image);
    }

    @Transactional(readOnly = true)
    public List<ProductImage> getImagesByProductId(String productId) {
        return productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
    }
}