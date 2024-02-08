package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.ItemFilter;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.ItemFilterDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ItemFilterMapper {

    @Mapping(target = "userId", expression = "java(toInteger(itemFilter.getUser()))")
    @Mapping(target = "isOwnedByEmployee", expression = "java(toString(itemFilter.getIsOwnedByEmployee()))")
    ItemFilterDto toItemFilterDto(ItemFilter itemFilter);

    @Mapping(target = "user", expression = "java(toUser(itemFilterDto.getUserId()))")
    @Mapping(target = "isOwnedByEmployee", expression = "java(toBoolean(itemFilterDto.getIsOwnedByEmployee()))")
    ItemFilter toItemFilter(ItemFilterDto itemFilterDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "isOwnedByEmployee", expression = "java(toBoolean(itemFilterDto.getIsOwnedByEmployee()))")
    ItemFilter copyItemFilterDtoToItemFilter(ItemFilterDto itemFilterDto, @MappingTarget ItemFilter itemFilter);

    default String toString(boolean b) {
        return b ? "Yes" : "No";
    }

    default boolean toBoolean(String s) {
        return "ON".equalsIgnoreCase(s);
    }

    default Integer toInteger(User user) {
        return user == null ? null : user.getId();
    }

    default User toUser(Integer id) {
        return id == null ? null : User.builder().id(id).build();
    }
}
