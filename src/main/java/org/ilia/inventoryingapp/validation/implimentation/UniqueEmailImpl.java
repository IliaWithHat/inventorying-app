package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.UserDto;
import org.ilia.inventoryingapp.validation.annotation.UniqueEmail;

import java.util.Optional;

@RequiredArgsConstructor
public class UniqueEmailImpl implements ConstraintValidator<UniqueEmail, UserDto> {

    private final UserRepository userRepository;

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext context) {
        Optional<User> user = userRepository.findUserByEmail(userDto.getEmail());
        return user.isEmpty() || user.get().getId().equals(userDto.getId());
    }
}
