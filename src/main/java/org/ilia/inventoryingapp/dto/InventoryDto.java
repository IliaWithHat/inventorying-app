package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import org.ilia.inventoryingapp.validation.annotation.InventoryNumberExist;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumber;

import java.math.BigDecimal;

@Value
@Builder
public class InventoryDto {

    Long id;

    @NotBlank(message = "Enter inventory number")
    @Size(max = 24, message = "The maximum length of the inventory number is 24 characters")
    @InventoryNumberExist
    @UniqueInventoryNumber
    String inventoryNumber;

    @NotNull(message = "Enter quantity")
    @PositiveOrZero(message = "Minimum item quantity is 0")
    @Max(value = 9_999_999, message = "Quantity must be less than 9_999_999")
    BigDecimal currentQuantity;

    @NotNull(message = "Enter price")
    @PositiveOrZero(message = "Minimum item price is 0")
    @Max(value = 9_999_999_999L, message = "Price must be less than 9_999_999_999")
    BigDecimal currentPrice;

    Integer userId;
}
