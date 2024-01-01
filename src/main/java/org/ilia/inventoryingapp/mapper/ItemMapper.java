package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "createdBy", expression = "java(toInteger(item.getCreatedBy()))")
    ItemDto toItemDto(Item item);

    @Mapping(target = "createdBy", expression = "java(toUser(itemDto.getCreatedBy()))")
    Item toItem(ItemDto itemDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Item copyItemDtoToItem(ItemDto itemDto, @MappingTarget Item item);

    default Integer toInteger(User user) {
        return user != null ? user.getId() : null;
    }

    default User toUser(Integer id) {
        return id != null ? User.builder().id(id).build() : null;
    }
}
