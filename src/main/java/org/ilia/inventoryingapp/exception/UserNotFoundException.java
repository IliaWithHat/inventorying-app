package org.ilia.inventoryingapp.exception;

import lombok.Getter;
import org.ilia.inventoryingapp.database.entity.User;

@Getter
public class UserNotFoundException extends Exception {

    private final Integer id;
    private final User user;

    public UserNotFoundException(Integer id, User user) {
        super(String.format("User with id %d not found for user with email %s ", id, user.getEmail()));
        this.id = id;
        this.user = user;
    }
}
