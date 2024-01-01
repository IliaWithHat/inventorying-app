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
    @Mapping(target = "modifiedBy", expression = "java(toInteger(item.getModifiedBy()))")
    ItemDto toItemDto(Item item);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", expression = "java(toUser(itemDto.getCreatedBy()))")
    @Mapping(target = "modifiedBy", expression = "java(toUser(itemDto.getModifiedBy()))")
    Item toItem(ItemDto itemDto);

    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", expression = "java(toUser(itemDto.getCreatedBy()))")
    @Mapping(target = "modifiedBy", expression = "java(toUser(itemDto.getModifiedBy()))")
    Item copyItemDtoToItem(ItemDto itemDto, @MappingTarget Item item);

    default Integer toInteger(User user) {
        if (user.getAdmin() != null) {
            return user.getAdmin().getId();
        }
        return null;
    }

    default User toUser(Integer id) {
        if (id != null) {
            return User.builder().id(id).build();
        }
        return null;
    }
}
