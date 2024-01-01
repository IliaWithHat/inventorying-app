package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "adminId", expression = "java(toInteger(user))")
    UserDto toUserDto(User user);

    @Mapping(target = "admin", expression = "java(toAdmin(userDto))")
    User toUser(UserDto userDto);

    @Mapping(target = "admin", expression = "java(toAdmin(userDto))")
    @Mapping(target = "id", ignore = true)
    User copyUserDtoToUser(UserDto userDto, @MappingTarget User user);

    default Integer toInteger(User user) {
        if (user.getAdmin() != null) {
            return user.getAdmin().getId();
        }
        return null;
    }

    default User toAdmin(UserDto userDto) {
        if (userDto.getAdminId() != null) {
            return User.builder().id(userDto.getAdminId()).build();
        }
        return null;
    }
}
