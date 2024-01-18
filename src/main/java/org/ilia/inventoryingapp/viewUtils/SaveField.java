package org.ilia.inventoryingapp.viewUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveField {

    private String saveName;
    private String saveInventoryNumber;
    private String autoincrement;
    private String saveStoredIn;
    private String saveUnit;
    private String saveQuantity;
    private String savePrice;
    private String saveIsOwnedByEmployee;
}
