package com.tcommerce.TCommerce.interfaces.validation.annotations;

import com.tcommerce.TCommerce.interfaces.validation.validators.ValidImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidImageValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidImage {
    String message() default "File must be a valid image (jpeg, png, gif, webp)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
