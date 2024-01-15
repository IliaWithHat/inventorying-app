package org.ilia.inventoryingapp.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.querydsl.QPredicates;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.database.repository.UserRepository;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.ilia.inventoryingapp.database.entity.QItem.item;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    public Page<ItemDto> findLastFiveItems(UserDetails userDetails) {
        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());
        Predicate predicate = QPredicates.builder()
                .add(userId, item.createdBy.id::eq)
                .build();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("serialNumber").descending());
        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    //TODO enable second level cache in Hibernate
    //TODO результаты с самым высоким совпадением должны быть на первом месте
    public Page<ItemDto> findAll(UserDetails userDetails, ItemFilter itemFilter, Integer page) {
        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());

        LocalDateTime showItemCreated = null;
        if (itemFilter.getShowItemCreated() != null && !"Ignore".equals(itemFilter.getShowItemCreated())) {
            switch (itemFilter.getShowItemCreated()) {
                case "1 day" -> showItemCreated = LocalDateTime.now().minusDays(1);
                case "3 day" -> showItemCreated = LocalDateTime.now().minusDays(3);
                case "1 week" -> showItemCreated = LocalDateTime.now().minusWeeks(1);
                case "2 week" -> showItemCreated = LocalDateTime.now().minusWeeks(2);
                case "1 month" -> showItemCreated = LocalDateTime.now().minusMonths(1);
                case "3 month" -> showItemCreated = LocalDateTime.now().minusMonths(3);
                case "6 month" -> showItemCreated = LocalDateTime.now().minusMonths(6);
                case "1 year" -> showItemCreated = LocalDateTime.now().minusYears(1);
            }
            itemFilter.setTimeIntervalStart(null);
            itemFilter.setTimeIntervalEnd(null);
        }

        Boolean isOwnedByEmployee = null;
        if (itemFilter.getIsOwnedByEmployee() != null) {
            switch (itemFilter.getIsOwnedByEmployee()) {
                case "Yes" -> isOwnedByEmployee = true;
                case "No" -> isOwnedByEmployee = false;
            }
        }

        Predicate predicate = QPredicates.builder()
                .add(userId, item.createdBy.id::eq)
                .add(itemFilter.getInventoryNumber(), item.inventoryNumber::eq)
                .add(itemFilter.getName(), item.name::containsIgnoreCase)
                .add(itemFilter.getStoredIn(), item.storedIn::containsIgnoreCase)
                .add(itemFilter.getTimeIntervalStart() == null ? null : itemFilter.getTimeIntervalStart().atStartOfDay(), item.createdAt::goe)
                .add(itemFilter.getTimeIntervalEnd() == null ? null : itemFilter.getTimeIntervalEnd().atTime(23, 59, 59), item.createdAt::loe)
                .add(showItemCreated, item.createdAt::goe)
                .add(isOwnedByEmployee, item.isOwnedByEmployee::eq)
                .build();
        Pageable pageable = PageRequest.of(page, 20, Sort.by("serialNumber"));
        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    public Optional<ItemDto> findById(Long id) {
        return itemRepository.findById(id)
                .map(itemMapper::toItemDto);
    }

    @Transactional
    public ItemDto create(UserDetails userDetails, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);

        item.setCreatedAt(LocalDateTime.now());
        Integer userId = userRepository.findUserIdByEmail(userDetails.getUsername());
        item.setCreatedBy(User.builder().id(userId).build());

        Item savedItem = itemRepository.save(item);
        return itemMapper.toItemDto(savedItem);
    }

    @Transactional
    public Optional<ItemDto> update(ItemDto itemDto, Long id) {
        return itemRepository.findById(id)
                .map(item -> itemMapper.copyItemDtoToItem(itemDto, item))
                .map(itemRepository::saveAndFlush)
                .map(itemMapper::toItemDto);
    }

    @Transactional
    public boolean delete(Long id) {
        if (itemRepository.findById(id).isPresent()) {
            itemRepository.deleteById(id);
            itemRepository.flush();
            return true;
        }
        return false;
    }

    public ItemDto saveStateOfFields(ItemDto itemDto, SaveField saveField) {
        String inventoryNumber;
        if (saveField.getAutoincrement() != null) {
            inventoryNumber = incrementStringNumber(itemDto.getInventoryNumber());
        } else {
            inventoryNumber = itemDto.getInventoryNumber();
        }
        return ItemDto.builder()
                .inventoryNumber(saveField.getSaveInventoryNumber() == null ? null : inventoryNumber)
                .name(saveField.getSaveName() == null ? null : itemDto.getName())
                .storedIn(saveField.getSaveStoredIn() == null ? null : itemDto.getStoredIn())
                .quantity(saveField.getSaveQuantity() == null ? null : itemDto.getQuantity())
                .additionalInfo(saveField.getSaveAdditionalInfo() == null ? null : itemDto.getAdditionalInfo())
                .isOwnedByEmployee(saveField.getSaveIsOwnedByEmployee() == null ? null : itemDto.getIsOwnedByEmployee())
                .build();
    }

    private String incrementStringNumber(String inventoryNumber) {
        try {
            if (inventoryNumber.contains(".")) {
                String numberBeforeDot = inventoryNumber.substring(0, inventoryNumber.lastIndexOf(".") + 1);
                String numberAfterDot = inventoryNumber.substring(inventoryNumber.lastIndexOf(".") + 1);
                long number = Long.parseLong(numberAfterDot) + 1;
                return numberBeforeDot + "0".repeat(numberAfterDot.length() - String.valueOf(number).length()) + number;
            } else if (inventoryNumber.contains("-")) {
                String numberBeforeHyphen = inventoryNumber.substring(0, inventoryNumber.indexOf("-") + 1);
                String numberAfterHyphen = inventoryNumber.substring(inventoryNumber.indexOf("-") + 1);
                long number = Long.parseLong(numberAfterHyphen) + 1;
                return numberBeforeHyphen + "0".repeat(numberAfterHyphen.length() - String.valueOf(number).length()) + number;
            } else if (inventoryNumber.charAt(0) == '0') {
                long number = Long.parseLong(inventoryNumber) + 1;
                return "0".repeat(inventoryNumber.length() - String.valueOf(number).length()) + number;
            } else {
                long number = Long.parseLong(inventoryNumber) + 1;
                return String.valueOf(number);
            }
        } catch (Exception e) {
            log.warn("Error increment this string: {}", inventoryNumber);
            return "";
        }
    }
}
