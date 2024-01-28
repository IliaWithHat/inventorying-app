package org.ilia.inventoryingapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;
import org.ilia.inventoryingapp.database.repository.InventoryRepository;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.mapper.InventoryMapper;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMapper inventoryMapper;
    private final ItemMapper itemMapper;

    public Page<ItemDto> findAll(UserDetails userDetails, ItemFilter itemFilter, Integer page) {
        return inventoryRepository.findItemsThatWereNotInventoried(itemFilter, userDetails, page)
                .map(itemMapper::toItemDto);
    }

    public InventoryDto create(InventoryDto inventoryDto) {
        Inventory inventory = inventoryMapper.toInventory(inventoryDto);

        Inventory savedInventory = inventoryRepository.save(inventory);
        return inventoryMapper.toInventoryDto(savedInventory);
    }

    public void deleteInventory(UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        inventoryRepository.deleteInventoryByUser(user);
    }

    public InventoryDto saveStateOfFields(InventoryDto inventoryDto, SaveField saveField) {
        return InventoryDto.builder()
                .inventoryNumber(saveField.getSaveInventoryNumber() == null ? null : inventoryDto.getInventoryNumber())
                .currentQuantity(saveField.getSaveQuantity() == null ? null : inventoryDto.getCurrentQuantity())
                .build();
    }
}
