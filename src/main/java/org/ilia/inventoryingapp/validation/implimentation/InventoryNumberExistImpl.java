package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.validation.annotation.InventoryNumberExist;
import org.springframework.security.core.context.SecurityContextHolder;

@RequiredArgsConstructor
public class InventoryNumberExistImpl implements ConstraintValidator<InventoryNumberExist, String> {

    private final ItemRepository itemRepository;

    @Override
    public boolean isValid(String inventoryNumber, ConstraintValidatorContext context) {
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        return itemRepository.findItemByInventoryNumberAndUser(inventoryNumber, user).isPresent();
    }
}
