package org.ilia.inventoryingapp.database.entity;

import org.springframework.security.core.GrantedAuthority;

//TODO ADMIN can create USER(can not create another USER), USER can create SLAVE(can do only inventorying);
public enum Role implements GrantedAuthority {
    ADMIN,
    USER;

    @Override
    public String getAuthority() {
        return name();
    }
}
