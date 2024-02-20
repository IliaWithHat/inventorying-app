package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.Inventory;
import org.ilia.inventoryingapp.dto.InventoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InventoryMapper extends ToIntegerToUserMapper {

    @Mapping(target = "user", expression = "java(toUser(inventoryDto.getUserId()))")
    Inventory toInventory(InventoryDto inventoryDto);

    @Mapping(target = "userId", expression = "java(toInteger(inventory.getUser()))")
    InventoryDto toInventoryDto(Inventory inventory);
}
