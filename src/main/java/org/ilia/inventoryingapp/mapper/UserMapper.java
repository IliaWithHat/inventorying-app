package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.config.SecurityConfiguration;
import org.ilia.inventoryingapp.database.entity.User;
import org.ilia.inventoryingapp.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.Optional;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "adminId", expression = "java(toInteger(user))")
    UserDto toUserDto(User user);

    @Mapping(target = "password", expression = "java(encodePassword(userDto))")
    @Mapping(target = "admin", expression = "java(toAdmin(userDto))")
    User toUser(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "admin", ignore = true)
    User copyUserDtoToUser(UserDto userDto, @MappingTarget User user);

    default String encodePassword(UserDto userDto) {
        PasswordEncoder passwordEncoder = new SecurityConfiguration().passwordEncoder();
        return Optional.ofNullable(userDto.getPassword())
                .filter(StringUtils::hasText)
                .map(passwordEncoder::encode)
                .orElse(null);
    }

    default Integer toInteger(User user) {
        return user.getAdmin() == null ? null : user.getAdmin().getId();
    }

    default User toAdmin(UserDto userDto) {
        return userDto.getAdminId() == null ? null : User.builder().id(userDto.getAdminId()).build();
    }
}
