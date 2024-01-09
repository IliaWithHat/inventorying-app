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

    @NotBlank(message = "Enter email")
    @Email(message = "Enter correct email")
    @UniqueEmail
    String email;

    @NotBlank(message = "Enter password")
    String password;

    @NotBlank(message = "Enter first name")
    String firstName;

    @NotBlank(message = "Enter middle name")
    String middleName;

    @NotBlank(message = "Enter last name")
    String lastName;

    @NotBlank(message = "Enter phone")
    String phone;

    Role role;

    Integer adminId;
}
