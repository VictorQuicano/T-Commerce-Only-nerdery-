package com.tcommerce.TCommerce.interfaces.validation.annotations;

import com.tcommerce.TCommerce.interfaces.validation.validators.MaxFileSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MaxFileSizeValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxFileSize {
    int value() default 3;
    String message() default "File size must not exceed {value} MB";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
