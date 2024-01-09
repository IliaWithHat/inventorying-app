package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.validation.annotation.UniqueEmail;

@RequiredArgsConstructor
public class UniqueEmailValidationImpl implements ConstraintValidator<UniqueEmail, String> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return userRepository.findUserByEmail(email).isEmpty();
    }
}
