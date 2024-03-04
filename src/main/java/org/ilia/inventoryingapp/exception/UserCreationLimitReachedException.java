package org.ilia.inventoryingapp.exception;

import lombok.Getter;
import org.ilia.inventoryingapp.database.entity.User;

@Getter
public class UserCreationLimitReachedException extends Exception {

    private final User user;

    public UserCreationLimitReachedException(User user) {
        super(String.format("This admin: %s has already created maximum users", user));
        this.user = user;
    }
}
