package org.ilia.inventoryingapp.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.querydsl.PredicateBuilder;
import org.ilia.inventoryingapp.database.querydsl.QPredicates;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.dto.UserDto;
import org.ilia.inventoryingapp.filter.ItemFilter;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.ilia.inventoryingapp.mapper.UserMapper;
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
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemSequenceService itemSequenceService;
    private final ItemMapper itemMapper;
    private final PredicateBuilder predicateBuilder;

    public Page<ItemDto> findLastFiveItems(UserDetails userDetails) {
        Integer userId = userService.findUserByEmail(userDetails.getUsername())
                .map(UserDto::getId)
                .orElseThrow();
        Predicate predicate = QPredicates.builder()
                .add(userId, item.createdBy.id::eq)
                .buildAnd();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("serialNumber").descending());
        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    public Page<ItemDto> findAll(UserDetails userDetails, ItemFilter itemFilter, Integer page) {
        Predicate predicate = predicateBuilder.buildPredicate(itemFilter, userDetails);

        Pageable pageable = PageRequest.of(page, 20, Sort.by("serialNumber"));
        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    public Optional<ItemDto> findById(Long id, UserDetails userDetails) {
        User user = userService.findUserByEmail(userDetails.getUsername())
                .map(userMapper::toUser)
                .orElseThrow();
        return itemRepository.findItemByIdAndCreatedBy(id, user)
                .map(itemMapper::toItemDto);
    }

    @Transactional
    public ItemDto create(UserDetails userDetails, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);

        //TODO enable auditing
        item.setCreatedAt(LocalDateTime.now());
        User user = userService.findUserByEmail(userDetails.getUsername())
                .map(userMapper::toUser)
                .orElseThrow();
        item.setCreatedBy(user);

        item.setSerialNumber(itemSequenceService.nextval(user.getEmail()));

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
                .name(saveField.getSaveName() == null ? null : itemDto.getName())
                .inventoryNumber(saveField.getSaveInventoryNumber() == null ? null : inventoryNumber)
                .storedIn(saveField.getSaveStoredIn() == null ? null : itemDto.getStoredIn())
                .unit(saveField.getSaveUnit() == null ? null : itemDto.getUnit())
                .quantity(saveField.getSaveQuantity() == null ? null : itemDto.getQuantity())
                .pricePerUnit(saveField.getSavePrice() == null ? null : itemDto.getPricePerUnit())
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
