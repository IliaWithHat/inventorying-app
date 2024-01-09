package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import org.ilia.inventoryingapp.database.entity.Role;
import org.ilia.inventoryingapp.validation.annotation.UniqueEmail;

@Value
@Builder
public class UserDto {

    Integer id;

    @Email
    @UniqueEmail
    String email;

    @NotBlank
    String password;

    @NotBlank
    String firstName;

    @NotBlank
    String middleName;

    @NotBlank
    String lastName;

    @NotBlank
    String phone;

    Role role;

    Integer adminId;
}
