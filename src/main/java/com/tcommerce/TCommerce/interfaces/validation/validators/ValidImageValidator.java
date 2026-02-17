package com.tcommerce.TCommerce.interfaces.validation.validators;

import com.tcommerce.TCommerce.interfaces.validation.annotations.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class ValidImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }
        return file.getContentType() != null && ALLOWED_CONTENT_TYPES.contains(file.getContentType());
    }
}
