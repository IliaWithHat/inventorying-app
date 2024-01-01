package org.ilia.inventoryingapp.dto;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

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

    Map<String, Object> additionalInfo;

    String image;

    Integer createdBy;

    Integer modifiedBy;
}
