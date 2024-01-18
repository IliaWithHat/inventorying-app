package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Value;
import org.ilia.inventoryingapp.validation.annotation.InventoryNumberExist;
import org.ilia.inventoryingapp.validation.annotation.UniqueInventoryNumber;

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
    @Positive(message = "Enter positive item quantity")
    Double currentQuantity;

    @NotNull(message = "Enter price")
    @PositiveOrZero(message = "Minimum item price is 0")
    Double currentPrice;

    Integer userId;
}
