package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.repository.InventoryRepositoryRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumber;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class UniqueInventoryNumberImpl implements ConstraintValidator<UniqueInventoryNumber, String> {

    private final InventoryRepositoryRepository inventoryRepository;
    private final UserRepository userRepository;

    @Override
    public boolean isValid(String inventoryNumber, ConstraintValidatorContext context) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findUserByEmail(email).orElseThrow();
        Optional<Inventory> inventory = inventoryRepository.findInventoryByInventoryNumberAndUser(inventoryNumber, user);
        return inventory.isEmpty();
    }
}
