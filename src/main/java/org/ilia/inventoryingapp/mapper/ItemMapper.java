package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.Item;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.ItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "isOwnedByEmployee", expression = "java(toStringIsOwnedByEmployee(item.getIsOwnedByEmployee()))")
    ItemDto toItemDto(Item item);

    @Mapping(target = "isOwnedByEmployee", expression = "java(toBooleanIsOwnedByEmployee(itemDto.getIsOwnedByEmployee()))")
    Item toItem(ItemDto itemDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "isOwnedByEmployee", expression = "java(toBooleanIsOwnedByEmployee(itemDto.getIsOwnedByEmployee()))")
    Item copyItemDtoToItem(ItemDto itemDto, @MappingTarget Item item);

    default String toStringAdditionalInfo(Map<String, String> map) {
        if (map == null)
            return null;
        return map.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";"));
    }

    default Map<String, String> toMapAdditionalInfo(String s) {
        if (s == null || s.isBlank())
            return null;
        HashMap<String, String> map = new HashMap<>();
        for (String strings : s.split(";")) {
            String[] keyValue = strings.split("=");
            if (keyValue[0].isBlank() || keyValue[1].isBlank())
                throw new RuntimeException("Value must me not empty");
            map.put(keyValue[0], keyValue[1]);
        }
        return map;
    }

    default String toStringIsOwnedByEmployee(boolean b) {
        return b ? "Yes" : "No";
    }

    default boolean toBooleanIsOwnedByEmployee(String s) {
        return "ON".equalsIgnoreCase(s);
    }

    default Integer toInteger(User user) {
        return user != null ? user.getId() : null;
    }

    default User toUser(Integer id) {
        return id != null ? User.builder().id(id).build() : null;
    }
}
