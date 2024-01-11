package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.Map;

@Value
@Builder
public class ItemDto {

    Long id;

    Long serialNumber;

    @NotNull(message = "Enter inventory number")
    @Min(value = 1, message = "Minimum number is 1")
    Long inventoryNumber;

    @NotBlank(message = "Enter item name")
    String name;

    @NotBlank(message = "Enter where item is located")
    String storedIn;

    @NotNull(message = "Enter quantity")
    @Min(value = 1,message = "Minimum item quantity is 1")
    Integer quantity;

    Boolean isOwnedByEmployee;

    Map<String, Object> additionalInfo;

    String image;

    LocalDateTime createdAt;

    Integer createdBy;
}
