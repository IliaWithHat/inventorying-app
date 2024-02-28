package org.ilia.inventoryingapp.database.entity;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class UserDetailsImpl extends org.springframework.security.core.userdetails.User {

    private final User user;

    public UserDetailsImpl(String username, String password, Role role, User user) {
        super(username, password, List.of(role));
        this.user = user;
    }
}
