package com.tcommerce.TCommerce.interfaces.validation.validators;

import com.tcommerce.TCommerce.interfaces.validation.annotations.ValidImageList;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public class ValidImageListValidator implements ConstraintValidator<ValidImageList, List<MultipartFile>> {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    private long maxSizeBytes;

    @Override
    public void initialize(ValidImageList annotation) {
        this.maxSizeBytes = annotation.maxSizeMB() * 1024L * 1024L;
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            System.out.println("✅ file type: " + file.getContentType());
            if (file.getContentType() == null || !ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                context.buildConstraintViolationWithTemplate(
                        String.format("File '%s' is not a valid image type. Allowed: jpeg, png, gif, webp",
                                file.getOriginalFilename()))
                        .addConstraintViolation();
                return false;
            }

            if (file.getSize() > maxSizeBytes) {
                context.buildConstraintViolationWithTemplate(
                        String.format("File '%s' exceeds the maximum size of %d MB",
                                file.getOriginalFilename(), maxSizeBytes / (1024 * 1024)))
                        .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
