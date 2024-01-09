package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.config.SecurityConfiguration;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "adminId", expression = "java(toInteger(user))")
    @Mapping(target = "password", ignore = true)
    UserDto toUserDto(User user);

    @Mapping(target = "admin", expression = "java(toAdmin(userDto))")
    @Mapping(target = "password", expression = "java(encodePassword(userDto))")
    User toUser(UserDto userDto);

    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    User copyUserDtoToUser(UserDto userDto, @MappingTarget User user);

    default String encodePassword(UserDto userDto) {
        PasswordEncoder passwordEncoder = new SecurityConfiguration().passwordEncoder();
        return passwordEncoder.encode(userDto.getPassword());
    }

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
