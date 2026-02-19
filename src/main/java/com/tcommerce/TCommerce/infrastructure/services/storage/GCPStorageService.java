package com.tcommerce.TCommerce.infrastructure.services.storage;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.WriteChannel;
import com.tcommerce.TCommerce.config.BucketConfig;
import com.tcommerce.TCommerce.domain.services.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;



@Service
public class GCPStorageService implements StorageService {

    private final Storage storage;
    private final BucketConfig bucketConfig;

    public GCPStorageService(Storage storage, BucketConfig bucketConfig) {
        this.storage = storage;
        this.bucketConfig = bucketConfig;
    }

    @Override
    public String uploadImage(MultipartFile file, String productId) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String blobName = String.format("%s/%s/%s%s",
                    bucketConfig.getSubdirectory() != null ? bucketConfig.getSubdirectory() : "images",
                    productId,
                    UUID.randomUUID(),
                    extension);

            BlobId blobId = BlobId.of(bucketConfig.getBucketName(), blobName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            try (WriteChannel writer = storage.writer(blobInfo);
                 InputStream is = file.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    writer.write(ByteBuffer.wrap(buffer, 0, bytesRead));
                }
            }

            return String.format("https://storage.googleapis.com/%s/%s", 
                    bucketConfig.getBucketName(), blobName);

        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to GCS", e);
        }
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files, String productId) {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        return files.stream()
                .map(file -> uploadImage(file, productId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String fileUrl) {
        URI uri = URI.create(fileUrl);
        String path = uri.getPath(); 
        String bucketName = bucketConfig.getBucketName();
        String blobName = path.substring(("/" + bucketName + "/").length()); 

        BlobId blobId = BlobId.of(bucketName, blobName);
        boolean deleted = storage.delete(blobId);
        if (!deleted) {
            throw new RuntimeException("Could not delete file: " + fileUrl);
        }
    }
}