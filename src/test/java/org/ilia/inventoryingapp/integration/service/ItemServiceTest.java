package org.ilia.inventoryingapp.integration.service;

import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.filter.ItemFilterForAdmin;
import org.ilia.inventoryingapp.integration.IntegrationTestBase;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.ilia.inventoryingapp.service.ItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilia.inventoryingapp.database.entity.OptionsForIsOwnedByEmployee.NO;
import static org.ilia.inventoryingapp.database.entity.Unit.PC;
import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest extends IntegrationTestBase {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemMapper itemMapper;

    @Test
    void findLastFiveItems() {
        assertThat(itemService.findLastFiveItems(userDetails))
                .hasSameElementsAs(items.stream()
                        .sorted((i1, i2) -> i2.getSerialNumber().compareTo(i1.getSerialNumber()))
                        .limit(5)
                        .map(itemMapper::toItemDto)
                        .toList());
    }

    @ParameterizedTest
    @MethodSource("findAllProvider")
    void findAll(ItemFilterForAdmin itemFilterForAdmin, int page, List<Item> expectedItems) {
        assertThat(itemService.findAll(userDetails, itemFilterForAdmin, page).stream().toList())
                .hasSameElementsAs(expectedItems.stream().map(itemMapper::toItemDto).toList());
    }

    public static Stream<Arguments> findAllProvider() {
        return Stream.of(
                Arguments.of(new ItemFilterForAdmin(), 0, items.subList(0, 20)),
                Arguments.of(new ItemFilterForAdmin(), 1, items.subList(20, 21)),
                Arguments.of(ItemFilterForAdmin.builder().name("Chair").build(), 0, items.stream().filter(i -> i.getName().equals("Chair")).toList()),
                Arguments.of(ItemFilterForAdmin.builder().isOwnedByEmployee(NO).build(), 0, items.stream().filter(i -> i.getIsOwnedByEmployee().equals(false)).toList()),
                Arguments.of(ItemFilterForAdmin.builder().storedIn("Warehouse").build(), 0, items.stream().filter(i -> i.getStoredIn().equals("Warehouse")).toList())
        );
    }

    @ParameterizedTest
    @MethodSource("findByIdProvider")
    void findById(long id, Optional<Item> expectedItem) {
        Optional<ItemDto> maybeItem = itemService.findById(id, userDetails);
        assertEquals(expectedItem.isPresent(), maybeItem.isPresent());
        assertEquals(expectedItem.map(itemMapper::toItemDto), maybeItem);
    }

    public static Stream<Arguments> findByIdProvider() {
        return Stream.of(
                Arguments.of(1L, Optional.of(items.getFirst())),
                Arguments.of(22L, Optional.empty())
        );
    }

    @Test
    void create() {
        ItemDto itemForCreation = ItemDto.builder()
                .serialNumber(22L)
                .inventoryNumber("22")
                .name("Item")
                .storedIn("Main Room")
                .unit(PC)
                .pricePerUnit(new BigDecimal("1.00"))
                .quantity(new BigDecimal("1.000"))
                .isOwnedByEmployee("ON")
                .build();

        assertTrue(itemService.findById(22L, userDetails).isEmpty());

        ItemDto createdItemDto = itemService.create(userDetails, itemForCreation);
        assertFalse(items.contains(itemMapper.toItem(createdItemDto)));

        assertEquals(createdItemDto, itemService.findById(22L, userDetails).get());
    }

    @Test
    void update() {
        Item itemForUpdate = itemMapper.toItem(itemService.findById(1L, userDetails).get());
        assertNotEquals("Updated Name", itemForUpdate.getName());

        itemForUpdate.setName("Updated Name");
        Item itemAfterUpdating = itemMapper.toItem(itemService.update(itemMapper.toItemDto(itemForUpdate), 1L, userDetails).get());

        assertEquals(itemForUpdate, itemAfterUpdating);

        assertEquals(itemForUpdate, itemMapper.toItem(itemService.findById(1L, userDetails).get()));
    }

    @Test
    void delete() {
        assertTrue(itemService.findById(1L, userDetails).isPresent());

        Optional<ItemDto> deletedItem = itemService.delete(1L, userDetails);
        assertTrue(deletedItem.isPresent());
        assertEquals(itemMapper.toItemDto(items.getFirst()), deletedItem.get());

        assertFalse(itemService.findById(1L, userDetails).isPresent());
    }
}