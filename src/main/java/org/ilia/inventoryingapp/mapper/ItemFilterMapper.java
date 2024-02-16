package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.ItemFilter;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ItemFilterMapper {

    @Mapping(target = "userId", expression = "java(toInteger(itemFilter.getUser()))")
    ItemFilterDto toItemFilterDto(ItemFilter itemFilter);

    @Mapping(target = "user", expression = "java(toUser(itemFilterDto.getUserId()))")
    ItemFilter toItemFilter(ItemFilterDto itemFilterDto);

    default Integer toInteger(User user) {
        return user == null ? null : user.getId();
    }

    default User toUser(Integer id) {
        return id == null ? null : User.builder().id(id).build();
    }
}
