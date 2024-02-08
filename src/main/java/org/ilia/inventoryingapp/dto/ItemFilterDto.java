package org.ilia.inventoryingapp.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemFilterDto {

    Integer id;

    @Size(max = 64, message = "The maximum length of a stored in field is 64 characters")
    String storedIn;

    String isOwnedByEmployee;

    Integer userId;
}
