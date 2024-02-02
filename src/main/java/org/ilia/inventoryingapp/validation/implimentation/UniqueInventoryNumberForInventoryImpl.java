package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;
import org.ilia.inventoryingapp.database.repository.InventoryRepository;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumberForInventory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class UniqueInventoryNumberForInventoryImpl implements ConstraintValidator<UniqueInventoryNumberForInventory, String> {

    private final InventoryRepository inventoryRepository;

    @Override
    public boolean isValid(String inventoryNumber, ConstraintValidatorContext context) {
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Optional<Inventory> inventory = inventoryRepository.findInventoryByInventoryNumberAndUser(inventoryNumber, user);
        return inventory.isEmpty();
    }
}