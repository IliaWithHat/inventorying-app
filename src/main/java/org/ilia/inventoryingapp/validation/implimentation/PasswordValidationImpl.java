package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.ilia.inventoryingapp.validation.annotation.PasswordValidation;

public class PasswordValidationImpl implements ConstraintValidator<PasswordValidation, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return "".equals(password) || !password.isBlank();
    }
}
