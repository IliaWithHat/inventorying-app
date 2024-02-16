package org.ilia.inventoryingapp.service;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.ItemFilter;
import org.ilia.inventoryingapp.database.repository.ItemFilterRepository;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.ilia.inventoryingapp.exception.UserNotFoundException;
import org.ilia.inventoryingapp.mapper.ItemFilterMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.ilia.inventoryingapp.filter.OptionsForIsOwnedByEmployee.IGNORE;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemFilterService {

    private final ItemFilterRepository itemFilterRepository;
    private final ItemFilterMapper itemFilterMapper;
    private final UserService userService;

    public void saveOrUpdate(Integer id, UserDetails userDetails, ItemFilterDto itemFilterDto) throws UserNotFoundException {
        if (userService.findById(id, userDetails).isEmpty() || !itemFilterDto.getUserId().equals(id))
            throw new UserNotFoundException(id, userDetails);

        List<ItemFilter> ItemFilterList = itemFilterRepository.findItemFilterByUserId(id);
        if (!ItemFilterList.isEmpty()) {
            delete(id, userDetails);
        }

        Arrays.stream(itemFilterDto.getStoredIn().split(";"))
                .filter(StringUtils::hasText)
                .map(s -> ItemFilterDto.builder()
                        .storedIn(s)
                        .isOwnedByEmployee(itemFilterDto.getIsOwnedByEmployee())
                        .userId(itemFilterDto.getUserId())
                        .build())
                .map(itemFilterMapper::toItemFilter)
                .toList()
                .forEach(itemFilterRepository::saveAndFlush);
    }

    public List<ItemFilterDto> findItemFilterListByUserId(Integer id) {
        List<ItemFilter> itemFilterList = itemFilterRepository.findItemFilterByUserId(id);
        if (itemFilterList.isEmpty()) {
            return Collections.singletonList(emptyItemFilterDto());
        }
        return itemFilterList.stream()
                .map(itemFilterMapper::toItemFilterDto)
                .toList();
    }

    public ItemFilterDto findItemFilterByUserId(Integer id) {
        List<ItemFilter> itemFilterList = itemFilterRepository.findItemFilterByUserId(id);
        if (itemFilterList.isEmpty()) {
            return emptyItemFilterDto();
        }

        String storedIn = itemFilterList.stream()
                .map(ItemFilter::getStoredIn)
                .collect(Collectors.joining(";"));
        return ItemFilterDto.builder()
                .storedIn(storedIn)
                .isOwnedByEmployee(itemFilterList.getFirst().getIsOwnedByEmployee())
                .userId(itemFilterList.getFirst().getUser().getId())
                .build();
    }

    private ItemFilterDto emptyItemFilterDto() {
        return ItemFilterDto.builder().storedIn("").isOwnedByEmployee(IGNORE).build();
    }

    public boolean delete(Integer id, UserDetails userDetails) throws UserNotFoundException {
        if (userService.findById(id, userDetails).isEmpty())
            throw new UserNotFoundException(id, userDetails);

        if (!itemFilterRepository.findItemFilterByUserId(id).isEmpty()) {
            itemFilterRepository.deleteByUserId(id);
            itemFilterRepository.flush();
            return true;
        }
        return false;
    }
}
