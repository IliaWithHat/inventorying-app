package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumberForEachUser;

import java.time.LocalDateTime;
import java.util.Map;

@Value
@Builder
public class ItemDto {

    Long id;

    //TODO unique serial number how in postgresql. New table VALUES(id, user_id, next_val)
    Long serialNumber;

    @NotNull(message = "Enter inventory number")
    @Min(value = 1, message = "Minimum number is 1")
    @UniqueInventoryNumberForEachUser
    Long inventoryNumber;

    @NotBlank(message = "Enter item name")
    String name;

    @NotBlank(message = "Enter where item is located")
    String storedIn;

    @NotNull(message = "Enter quantity")
    @Min(value = 1, message = "Minimum item quantity is 1")
    Integer quantity;

    String isOwnedByEmployee;

    Map<String, Object> additionalInfo;

    String image;

    LocalDateTime createdAt;

    Integer createdBy;
}
