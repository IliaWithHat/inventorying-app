package org.ilia.inventoryingapp.exception;

import lombok.Getter;
import org.ilia.inventoryingapp.database.entity.UserDetailsImpl;

@Getter
public class UserNotFoundException extends Exception {

    private final Integer id;

    private final UserDetailsImpl userDetails;

    public UserNotFoundException(Integer id, UserDetailsImpl userDetails) {
        super(String.format("User with id %d not found for user with email %s ", id, userDetails.getUser().getEmail()));
        this.id = id;
        this.userDetails = userDetails;
    }
}
