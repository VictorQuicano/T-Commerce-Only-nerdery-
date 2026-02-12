package com.tcommerce.TCommerce.domain.services;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface StorageService {
    String uploadImage(MultipartFile file, String productId);
    List<String> uploadImages(List<MultipartFile> files, String productId);
    void deleteFile(String fileUrl);
}