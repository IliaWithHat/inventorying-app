package org.ilia.inventoryingapp.unit.mapper;

import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.Role;
import org.ilia.inventoryingapp.database.entity.Unit;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemMapperTest {

    private final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);

    private User user = new User(1, "test", "{noop}1234", "Ilia", "Rozhko", "+380-96-873-77-76", Role.ADMIN, null);

    private final List<Item> itemList = List.of(
            new Item(11L, 11L, "Cable", "00000011", "Warehouse", Unit.M, new BigDecimal("8.550"), new BigDecimal("70.12"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(12L, 12L, "Net", "00000012", "Warehouse", Unit.M2, new BigDecimal("0.400"), new BigDecimal("0.01"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(13L, 13L, "Glasses", "00000013", "Warehouse", Unit.PC, new BigDecimal("1.000"), new BigDecimal("15.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(14L, 14L, "Engine", "00000014", "Warehouse", Unit.PC, new BigDecimal("1.000"), new BigDecimal("3000.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user),
            new Item(15L, 15L, "Thermometer", "00000015", "Warehouse", Unit.PC, new BigDecimal("10.000"), new BigDecimal("13.00"), false, LocalDateTime.of(2024, 1, 19, 0, 0, 0), user)
    );

    private final List<ItemDto> itemDtoList = List.of(
            new ItemDto(11L, 11L, "Cable", "00000011", "Warehouse", Unit.M, new BigDecimal("8.550"), new BigDecimal("70.12"), new BigDecimal("599.53"), "No", LocalDateTime.of(2024, 1, 19, 0, 0, 0), 1),
            new ItemDto(12L, 12L, "Net", "00000012", "Warehouse", Unit.M2, new BigDecimal("0.400"), new BigDecimal("0.01"), new BigDecimal("0.00"), "No", LocalDateTime.of(2024, 1, 19, 0, 0, 0), 1),
            new ItemDto(13L, 13L, "Glasses", "00000013", "Warehouse", Unit.PC, new BigDecimal("1.000"), new BigDecimal("15.00"), new BigDecimal("15.00"), "No", LocalDateTime.of(2024, 1, 19, 0, 0, 0), 1),
            new ItemDto(14L, 14L, "Engine", "00000014", "Warehouse", Unit.PC, new BigDecimal("1.000"), new BigDecimal("3000.00"), new BigDecimal("3000.00"), "No", LocalDateTime.of(2024, 1, 19, 0, 0, 0), 1),
            new ItemDto(15L, 15L, "Thermometer", "00000015", "Warehouse", Unit.PC, new BigDecimal("10.000"), new BigDecimal("13.00"), new BigDecimal("130.00"), "No", LocalDateTime.of(2024, 1, 19, 0, 0, 0), 1)
    );

    @Test
    void toItemDto() {
        assertThat(itemList.stream()
                .map(itemMapper::toItemDto)
                .toList())
                .hasSameElementsAs(itemDtoList);
    }

    @Test
    void toItem() {
        assertThat(itemDtoList.stream()
                .map(itemMapper::toItem)
                .toList())
                .hasSameElementsAs(itemList);
    }
}