package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumberForEachUser;

import java.time.LocalDateTime;

@Value
@Builder
@UniqueInventoryNumberForEachUser
public class ItemDto {

    Long id;

    //TODO unique serial number how in postgresql. New table VALUES(id, user_id, last_value)
    Long serialNumber;

    @Size(max = 24, message = "The maximum length of the inventory number is 24 characters")
    @NotBlank(message = "Enter inventory number")
    String inventoryNumber;

    @Size(max = 64, message = "The maximum length of an item name is 64 characters")
    @NotBlank(message = "Enter item name")
    String name;

    @Size(max = 64, message = "The maximum length of a stored in field is 64 characters")
    @NotBlank(message = "Enter where item is located")
    String storedIn;

    @NotNull(message = "Enter quantity")
    @Min(value = 1, message = "Minimum item quantity is 1")
    Integer quantity;

    String isOwnedByEmployee;

    @Size(max = 128, message = "The maximum length of additional information is 128 characters")
    String additionalInfo;

//    String image;

    LocalDateTime createdAt;

    Integer createdBy;
}
