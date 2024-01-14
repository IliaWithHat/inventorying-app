package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 64, message = "The maximum length of email is 64 characters")
    @UniqueEmail
    String email;

    @NotBlank(message = "Enter password")
    String password;

    @NotBlank(message = "Enter first name")
    @Size(max = 64, message = "The maximum length of first name is 64 characters")
    String firstName;

    @NotBlank(message = "Enter middle name")
    @Size(max = 64, message = "The maximum length of middle name is 64 characters")
    String middleName;

    @NotBlank(message = "Enter last name")
    @Size(max = 64, message = "The maximum length of last name is 64 characters")
    String lastName;

    @NotBlank(message = "Enter phone")
    @Size(max = 64, message = "The maximum length of a phone number is 64 characters")
    String phone;

    Role role;

    Integer adminId;
}
