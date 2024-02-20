package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.ItemFilter;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemFilterMapper extends ToIntegerToUserMapper {

    @Mapping(target = "userId", expression = "java(toInteger(itemFilter.getUser()))")
    ItemFilterDto toItemFilterDto(ItemFilter itemFilter);

    @Mapping(target = "user", expression = "java(toUser(itemFilterDto.getUserId()))")
    ItemFilter toItemFilter(ItemFilterDto itemFilterDto);
}
