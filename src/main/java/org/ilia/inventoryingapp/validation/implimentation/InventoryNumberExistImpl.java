package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.validation.annotation.InventoryNumberExist;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class InventoryNumberExistImpl implements ConstraintValidator<InventoryNumberExist, String> {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public boolean isValid(String inventoryNumber, ConstraintValidatorContext context) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userRepository.findUserIdByEmail(email);
        Optional<Item> item = itemRepository.findItemByInventoryNumberAndCreatedBy(inventoryNumber, userId);
        return item.isPresent();
    }
}
