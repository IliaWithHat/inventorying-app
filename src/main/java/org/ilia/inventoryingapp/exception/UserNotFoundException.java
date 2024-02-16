package org.ilia.inventoryingapp.exception;

import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserNotFoundException extends Exception {

    private final Integer id;

    private final UserDetails userDetails;

    public UserNotFoundException(Integer id, UserDetails userDetails) {
        super("User with id " + id + " not found for user " + userDetails.getUsername());
        this.id = id;
        this.userDetails = userDetails;
    }
}
