package org.ilia.inventoryingapp.dto;

import lombok.Builder;
import lombok.Value;
import org.ilia.inventoryingapp.database.entity.Role;

@Value
@Builder
public class UserDto {

    Integer id;

    String email;

    String firstName;

    String middleName;

    String lastName;

    String phone;

    Role role;

    Integer adminId;
    
}
