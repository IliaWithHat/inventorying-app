package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import org.ilia.inventoryingapp.database.entity.Unit;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumberForUser;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
@UniqueInventoryNumberForUser
public class ItemDto {

    Long id;

    Long serialNumber;

    @Size(max = 64, message = "The maximum length of an item name is 64 characters")
    @NotBlank(message = "Enter item name")
    String name;

    @Size(max = 24, message = "The maximum length of the inventory number is 24 characters")
    @NotBlank(message = "Enter inventory number")
    String inventoryNumber;

    @Size(max = 64, message = "The maximum length of a stored in field is 64 characters")
    @NotBlank(message = "Enter where item is located")
    String storedIn;

    Unit unit;

    @NotNull(message = "Enter quantity")
    @Positive(message = "Enter positive item quantity")
    @Max(value = 9_999_999, message = "Quantity must be less than 9,999,999")
    BigDecimal quantity;

    @NotNull(message = "Enter price")
    @PositiveOrZero(message = "Minimum item price is 0")
    @Max(value = 99_999_999L, message = "Price must be less than 99,999,999")
    BigDecimal pricePerUnit;

    BigDecimal sum;

    String isOwnedByEmployee;

    LocalDateTime createdAt;

    Integer userId;
}
