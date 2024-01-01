package org.ilia.inventoryingapp.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemDto {

    Long id;

    Long serialNumber;

    Long inventoryNumber;

    String name;

    String storedIn;

    Integer quantity;

    Boolean isOwnedByEmployee;

    String additionalInfo;

    String image;

    Integer createdBy;

    Integer modifiedBy;
}
