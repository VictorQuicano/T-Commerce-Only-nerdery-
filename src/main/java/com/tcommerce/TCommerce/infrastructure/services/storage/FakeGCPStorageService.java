package com.tcommerce.TCommerce.infrastructure.services.storage;

import com.tcommerce.TCommerce.domain.services.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Primary;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FakeGCPStorageService implements StorageService {

    @Override
    public String uploadImage(MultipartFile file, String productId) {
        if (file.isEmpty()) {
            return null;
        }
        
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String bucketName = "FAKE-Bucket"; // Replace with your bucket name
        
        System.out.println("Uploading file " + file.getOriginalFilename() + " to GCP bucket " + bucketName);
        
        return String.format("https://fake-storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files, String productId) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        return files.stream()
                .map(file -> uploadImage(file, productId))
                .filter(url -> url != null)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String fileUrl) {
        System.out.println("Deleting file from GCP: " + fileUrl);
    }
}