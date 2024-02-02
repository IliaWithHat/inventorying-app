package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumberForEachUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
public class UniqueInventoryNumberForEachUserImpl implements ConstraintValidator<UniqueInventoryNumberForEachUser, ItemDto> {

    private final ItemRepository itemRepository;

    @Override
    public boolean isValid(ItemDto itemDto, ConstraintValidatorContext context) {
        User user = ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        Optional<Item> item = itemRepository.findItemByInventoryNumberAndUser(itemDto.getInventoryNumber(), user);
        return item.isEmpty() || item.get().getId().equals(itemDto.getId());
    }
}
