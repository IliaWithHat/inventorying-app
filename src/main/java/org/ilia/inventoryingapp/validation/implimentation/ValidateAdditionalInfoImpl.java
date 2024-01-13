package org.ilia.inventoryingapp.validation.implimentation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.ilia.inventoryingapp.mapper.ItemMapper;
import org.ilia.inventoryingapp.validation.annotation.ValidateAdditionalInfo;

@RequiredArgsConstructor
public class ValidateAdditionalInfoImpl implements ConstraintValidator<ValidateAdditionalInfo, String> {

    private final ItemMapper itemMapper;

    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        try {
            itemMapper.toMapAdditionalInfo(str);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
