package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumberForEachUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class UniqueInventoryNumberForEachUserImpl implements ConstraintValidator<UniqueInventoryNumberForEachUser, ItemDto> {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public boolean isValid(ItemDto itemDto, ConstraintValidatorContext context) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer userId = userRepository.findUserIdByEmail(email);
        Optional<Item> item = itemRepository.findItemByInventoryNumberAndCreatedBy(itemDto.getInventoryNumber(), userId);
        return item.isEmpty() || item.get().getId().equals(itemDto.getId());
    }
}