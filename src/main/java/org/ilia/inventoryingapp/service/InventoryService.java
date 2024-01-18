package org.ilia.inventoryingapp.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.querydsl.BuildPredicate;
import org.ilia.inventoryingapp.database.repository.InventoryRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.mapper.InventoryMapper;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final BuildPredicate buildPredicate;
    private final UserRepository userRepository;

    public InventoryDto create(UserDetails userDetails, InventoryDto inventoryDto) {
        Inventory inventory = inventoryMapper.toInventory(inventoryDto);

        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());
        inventory.setUser(User.builder().id(userId).build());

        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toInventoryDto(savedInventory);
    }

    public void deleteInventoryByUserDetails(UserDetails userDetails) {
        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());
        inventoryRepository.deleteInventoryByUserId(userId);
    }

    public Resource generateFullPdfByItemFilterAndUserDetails(ItemFilter itemFilter, UserDetails userDetails) {
        Predicate predicate = buildPredicate.buildPredicateByItemFilter(itemFilter, userDetails);
        return null;
    }
}
