package org.ilia.inventoryingapp.database.entity;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@ToString
public class UserDetailsImpl extends org.springframework.security.core.userdetails.User {

    private final User user;

    public UserDetailsImpl(String username, String password, Collection<? extends GrantedAuthority> authorities, User user) {
        super(username, password, authorities);
        this.user = user;
    }
}
