package com.tcommerce.TCommerce.interfaces.validation.annotations;

import com.tcommerce.TCommerce.interfaces.validation.validators.ValidImageListValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidImageListValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImageList {
    int maxSizeMB() default 3;
    String message() default "All files must be valid images (jpeg, png, gif, webp) and not exceed {maxSizeMB} MB each";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
