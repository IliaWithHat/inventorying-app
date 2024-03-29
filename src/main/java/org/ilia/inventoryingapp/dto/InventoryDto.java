package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import org.ilia.inventoryingapp.validation.annotation.InventoryNumberExist;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventory;

import java.math.BigDecimal;

@Value
@Builder
public class InventoryDto {

    Long id;

    @NotBlank(message = "Enter inventory number")
    @Size(max = 24, message = "The maximum length of the inventory number is 24 characters")
    @InventoryNumberExist
    @UniqueInventory
    String inventoryNumber;

    @NotNull(message = "Enter quantity")
    @PositiveOrZero(message = "Minimum item quantity is 0")
    @Max(value = 9_999_999, message = "Quantity must be less than 9,999,999")
    BigDecimal currentQuantity;

    Integer userId;
}
