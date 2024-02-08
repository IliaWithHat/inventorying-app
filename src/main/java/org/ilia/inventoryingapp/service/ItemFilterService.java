package org.ilia.inventoryingapp.service;

import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.database.entity.ItemFilter;
import org.ilia.inventoryingapp.database.repository.ItemFilterRepository;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.ilia.inventoryingapp.mapper.ItemFilterMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.ilia.inventoryingapp.filter.OptionsForIsOwnedByEmployee.IGNORE;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemFilterService {

    private final ItemFilterRepository itemFilterRepository;
    private final ItemFilterMapper itemFilterMapper;
    private final UserService userService;

    public void saveOrUpdate(Integer id, UserDetails userDetails, ItemFilterDto itemFilterDto) {
        userService.findById(id, userDetails)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Optional<ItemFilter> maybeItemFilter = itemFilterRepository.findItemFilterByUserId(id);
        if (maybeItemFilter.isEmpty()) {
            Optional.of(itemFilterDto)
                    .map(itemFilterMapper::toItemFilter)
                    .map(itemFilterRepository::saveAndFlush);
        } else {
            maybeItemFilter
                    .map(i -> itemFilterMapper.copyItemFilterDtoToItemFilter(itemFilterDto, i))
                    .map(itemFilterRepository::saveAndFlush);
        }
    }

    public ItemFilterDto findByUserId(Integer id) {
        return findByUserId(id, null);
    }

    public ItemFilterDto findByUserId(Integer id, UserDetails userDetails) {
        if (userDetails != null) {
            userService.findById(id, userDetails).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }
        return itemFilterRepository.findItemFilterByUserId(id)
                .map(itemFilterMapper::toItemFilterDto)
                .orElse(emptyItemFilterDto());
    }

    private ItemFilterDto emptyItemFilterDto() {
        return ItemFilterDto.builder().storedIn(null).isOwnedByEmployee(IGNORE).build();
    }

    public boolean delete(Integer id, UserDetails userDetails) {
        userService.findById(id, userDetails)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (itemFilterRepository.findItemFilterByUserId(id).isPresent()) {
            itemFilterRepository.deleteByUserId(id);
            itemFilterRepository.flush();
            return true;
        }
        return false;
    }
}
