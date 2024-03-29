package org.ilia.inventoryingapp.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;
import org.ilia.inventoryingapp.database.querydsl.PredicateBuilder;
import org.ilia.inventoryingapp.database.querydsl.QPredicates;
import org.ilia.inventoryingapp.database.repository.ItemRepository;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
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

import java.util.Optional;

import static org.ilia.inventoryingapp.database.entity.QItem.item;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemSequenceService itemSequenceService;
    private final ItemMapper itemMapper;
    private final PredicateBuilder predicateBuilder;
    private final GeneratePdf generatePdf;

    public Page<ItemDto> findLastFiveItems(UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();

        Predicate predicate = QPredicates.builder()
                .add(user, item.user::eq)
                .buildAnd();
        Pageable pageable = PageRequest.of(0, 5, Sort.by("serialNumber").descending());

        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    public Page<ItemDto> findAll(UserDetails userDetails, ItemFilterForAdmin itemFilterForAdmin, Integer page) {
        User user = ((UserDetailsImpl) userDetails).getUser();

        Predicate predicate = predicateBuilder.buildPredicate(user, itemFilterForAdmin);
        Pageable pageable = PageRequest.of(page, 20, Sort.by("serialNumber"));
        return itemRepository.findAll(predicate, pageable)
                .map(itemMapper::toItemDto);
    }

    public Optional<ItemDto> findById(Long id, UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        return itemRepository.findItemByIdAndUser(id, user)
                .map(itemMapper::toItemDto);
    }

    @Transactional
    public ItemDto create(UserDetails userDetails, ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);

        item.setSerialNumber(itemSequenceService.nextval(userDetails));

        itemRepository.save(item);
        return itemMapper.toItemDto(item);
    }

    @Transactional
    public Optional<ItemDto> update(ItemDto itemDto, Long id, UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        return itemRepository.findItemByIdAndUser(id, user)
                .map(item -> itemMapper.copyItemDtoToItem(itemDto, item))
                .map(itemRepository::saveAndFlush)
                .map(itemMapper::toItemDto);
    }

    @Transactional
    public Optional<ItemDto> delete(Long id, UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        return itemRepository.findItemByIdAndUser(id, user)
                .map(item -> {
                    itemRepository.delete(item);
                    itemRepository.flush();
                    return itemMapper.toItemDto(item);
                });
    }

    public Resource getPdf(ItemFilterForAdmin itemFilterForAdmin, UserDetails userDetails) {
        User user = ((UserDetailsImpl) userDetails).getUser();
        return generatePdf.generateStandardPdf(itemFilterForAdmin, user);
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
