package org.ilia.inventoryingapp.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;
import org.ilia.inventoryingapp.database.querydsl.PredicateBuilder;
import org.ilia.inventoryingapp.database.repository.InventoryRepository;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
import org.ilia.inventoryingapp.mapper.InventoryMapper;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.ilia.inventoryingapp.pdf.GeneratePdf;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final PredicateBuilder predicateBuilder;
    private final GeneratePdf generatePdf;

    public Page<ItemDto> findAll(UserDetails userDetails, ItemFilterForAdmin itemFilterForAdmin, Integer page) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        Pageable pageable = PageRequest.of(page, 20, Sort.by("serialNumber"));
        Predicate predicate = predicateBuilder.buildPredicate(user, itemFilterForAdmin);
        return inventoryRepository.findItemsThatWereNotInventoried(predicate, pageable, user)
                .map(itemMapper::toItemDto);
    }

    public InventoryDto create(InventoryDto inventoryDto) {
        Inventory inventory = inventoryMapper.toInventory(inventoryDto);
        inventoryRepository.save(inventory);
        return inventoryMapper.toInventoryDto(inventory);
    }

    public void deleteAllInventory(UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        inventoryRepository.deleteInventoryByUser(user);
    }

    public InventoryDto saveStateOfFields(InventoryDto inventoryDto, SaveField saveField) {
        return InventoryDto.builder()
                .inventoryNumber(saveField.getSaveInventoryNumber() == null ? null : inventoryDto.getInventoryNumber())
                .currentQuantity(saveField.getSaveQuantity() == null ? null : inventoryDto.getCurrentQuantity())
                .build();
    }

    public Resource getPdf(ItemFilterForAdmin itemFilterForAdmin, UserDetails userDetails, String inventoryMethod) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        return generatePdf.generateInventoryPdf(itemFilterForAdmin, user, inventoryMethod);
    }
}
