package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumberForEachUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@RequiredArgsConstructor
public class UniqueInventoryNumberForEachUserImpl implements ConstraintValidator<UniqueInventoryNumberForEachUser, Long> {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public boolean isValid(Long inventoryNumber, ConstraintValidatorContext context) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userRepository.findUserIdByEmail(email);
        Optional<Item> item = itemRepository.findItemByInventoryNumberAndCreatedBy(inventoryNumber, userId);
        return item.isEmpty();
    }
}
