package org.ilia.inventoryingapp.mapper;

import org.ilia.inventoryingapp.database.entity.User;

public interface ToIntegerToUserMapper {

    default Integer toInteger(User user) {
        return user == null ? null : user.getId();
    }

    default User toUser(Integer id) {
        return id == null ? null : User.builder().id(id).build();
    }
}
