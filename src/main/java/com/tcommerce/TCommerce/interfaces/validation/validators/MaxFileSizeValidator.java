package com.tcommerce.TCommerce.interfaces.validation.validators;

import com.tcommerce.TCommerce.interfaces.validation.annotations.MaxFileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class MaxFileSizeValidator implements ConstraintValidator<MaxFileSize, MultipartFile> {

    private long maxSizeBytes;

    @Override
    public void initialize(MaxFileSize annotation) {
        this.maxSizeBytes = annotation.value() * 1024L * 1024L;
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        return file.getSize() <= maxSizeBytes;
    }
}
