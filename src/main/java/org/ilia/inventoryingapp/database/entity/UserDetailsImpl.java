package org.ilia.inventoryingapp.database.entity;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UserDetailsImpl extends org.springframework.security.core.userdetails.User {

    private final User user;

    public UserDetailsImpl(User user) {
        super(user.getEmail(), user.getPassword(), List.of(user.getRole()));
        this.user = user;
    }
}
