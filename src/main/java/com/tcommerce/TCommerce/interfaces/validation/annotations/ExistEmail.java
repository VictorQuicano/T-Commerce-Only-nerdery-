package com.tcommerce.TCommerce.interfaces.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import com.tcommerce.TCommerce.interfaces.validation.validators.ExistEmailValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistEmailValidator.class)
public @interface ExistEmail {
    String message() default "Email does not exist";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
