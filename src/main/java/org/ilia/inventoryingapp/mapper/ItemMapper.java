package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "isOwnedByEmployee", expression = "java(toString(item.getIsOwnedByEmployee()))")
    ItemDto toItemDto(Item item);

    @Mapping(target = "isOwnedByEmployee", expression = "java(toBoolean(itemDto.getIsOwnedByEmployee()))")
    Item toItem(ItemDto itemDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "isOwnedByEmployee", expression = "java(toBoolean(itemDto.getIsOwnedByEmployee()))")
    Item copyItemDtoToItem(ItemDto itemDto, @MappingTarget Item item);

    default String toString(boolean b) {
        return b ? "Yes" : "No";
    }

    default boolean toBoolean(String s) {
        return "ON".equalsIgnoreCase(s);
    }

    default Integer toInteger(User user) {
        return user != null ? user.getId() : null;
    }

    default User toUser(Integer id) {
        return id != null ? User.builder().id(id).build() : null;
    }
}
