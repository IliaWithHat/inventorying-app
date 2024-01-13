package org.ilia.inventoryingapp.filter;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFilter {

    private Long inventoryNumber;
    private String name;
    private String storedIn;
    private String isOwnedByEmployee;
//    private LocalDate createdAtStart;
}
