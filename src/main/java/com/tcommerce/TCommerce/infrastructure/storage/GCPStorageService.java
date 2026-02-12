package com.tcommerce.TCommerce.infrastructure.storage;

import com.tcommerce.TCommerce.domain.services.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GCPStorageService implements StorageService {

    // TODO: Add Google Cloud Storage dependency and inject Storage object
    // private final Storage storage;

    @Override
    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }
        
        // Simulating upload to GCP bucket
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String bucketName = "tcommerce-products"; // Replace with your bucket name
        
        System.out.println("Uploading file " + file.getOriginalFilename() + " to GCP bucket " + bucketName);
        
        // Return dummy GCP URL
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        return files.stream()
                .map(this::uploadFile)
                .filter(url -> url != null)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String fileUrl) {
        // TODO: Implement deletion from GCP
        System.out.println("Deleting file from GCP: " + fileUrl);
    }
}
