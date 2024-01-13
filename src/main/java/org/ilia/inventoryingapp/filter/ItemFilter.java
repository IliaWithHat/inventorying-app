package org.ilia.inventoryingapp.filter;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFilter {

    private Long inventoryNumber;
    private String name;
    private String storedIn;
    private LocalDate timeIntervalStart;
    private LocalDate timeIntervalEnd;
    private String showItemCreated;
    private String isOwnedByEmployee;
}
