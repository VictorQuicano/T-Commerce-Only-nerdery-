package com.tcommerce.TCommerce.interfaces.validation.validators;


import com.tcommerce.TCommerce.domain.repositories.interfaces.auth.UserRepository;
import com.tcommerce.TCommerce.interfaces.validation.annotations.ExistEmail;
import jakarta.validation.ConstraintValidator;

import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExistEmailValidator implements ConstraintValidator<ExistEmail, String> {
    private final UserRepository userRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userRepository.existsByEmail(value);
    }
}
