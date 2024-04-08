package org.ilia.inventoryingapp.unit.service;

import org.ilia.inventoryingapp.dto.ItemDto;
import org.ilia.inventoryingapp.service.ItemService;
import org.ilia.inventoryingapp.viewUtils.SaveField;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.ilia.inventoryingapp.database.entity.Unit.PC;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemServiceTest {

    private final ItemService itemService = new ItemService(null, null, null, null, null);

    private static final ItemDto itemForSave = ItemDto.builder()
            .serialNumber(22L)
            .inventoryNumber("22")
            .name("Item")
            .storedIn("Main Room")
            .unit(PC)
            .pricePerUnit(new BigDecimal("1.00"))
            .quantity(new BigDecimal("1.000"))
            .isOwnedByEmployee(null)
            .build();

    @ParameterizedTest
    @MethodSource("saveStateOfFieldsProvider")
    void saveStateOfFields(SaveField saveField, ItemDto expectedItemDto) {
        assertEquals(expectedItemDto, itemService.saveStateOfFields(itemForSave, saveField));
    }

    public static Stream<Arguments> saveStateOfFieldsProvider() {
        return Stream.of(
                Arguments.of(SaveField.builder().build(), ItemDto.builder().build()),
                Arguments.of(SaveField.builder().saveInventoryNumber("ON").autoincrement("ON").build(), ItemDto.builder().inventoryNumber("23").build()),
                Arguments.of(SaveField.builder().autoincrement("ON").build(), ItemDto.builder().build()),
                Arguments.of(SaveField.builder().saveName("ON").build(), ItemDto.builder().name(itemForSave.getName()).build()),
                Arguments.of(SaveField.builder().savePrice("ON").build(), ItemDto.builder().pricePerUnit(itemForSave.getPricePerUnit()).build()),
                Arguments.of(SaveField.builder().saveStoredIn("ON").build(), ItemDto.builder().storedIn(itemForSave.getStoredIn()).build()),
                Arguments.of(SaveField.builder().saveIsOwnedByEmployee("ON").build(), ItemDto.builder().isOwnedByEmployee(itemForSave.getIsOwnedByEmployee()).build()),
                Arguments.of(SaveField.builder().saveIsOwnedByEmployee("ON").saveName("ON").build(), ItemDto.builder().isOwnedByEmployee(itemForSave.getIsOwnedByEmployee()).name(itemForSave.getName()).build())
        );
    }
}