package com.tcommerce.TCommerce.interfaces.validation.annotations;

import com.tcommerce.TCommerce.interfaces.validation.validators.StrongPasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Password must be 8-20 characters with at least one uppercase, one lowercase, and one special character";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
