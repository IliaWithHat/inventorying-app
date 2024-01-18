package org.ilia.inventoryingapp.filter;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFilter {

    private String name;
    private String inventoryNumber;
    private String storedIn;
    private LocalDate timeIntervalStart;
    private LocalDate timeIntervalEnd;
    private String showItemCreated;
    private String isOwnedByEmployee;

    public void resetFilter() {
        this.name = null;
        this.inventoryNumber = null;
        this.storedIn = null;
        this.timeIntervalStart = null;
        this.timeIntervalEnd = null;
        this.showItemCreated = null;
        this.isOwnedByEmployee = null;
    }
}
