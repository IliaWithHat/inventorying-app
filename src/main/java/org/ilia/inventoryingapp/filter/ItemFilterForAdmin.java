package org.ilia.inventoryingapp.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ilia.inventoryingapp.database.entity.OptionsForIsOwnedByEmployee;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFilterForAdmin {

    private String name;
    private String inventoryNumber;
    private String storedIn;
    private LocalDate timeIntervalStart;
    private LocalDate timeIntervalEnd;
    private TimeDurationEnum showItemCreated;
    private OptionsForIsOwnedByEmployee isOwnedByEmployee;
}
