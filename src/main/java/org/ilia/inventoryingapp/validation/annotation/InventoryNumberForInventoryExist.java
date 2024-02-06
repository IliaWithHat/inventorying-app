package org.ilia.inventoryingapp.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.inventoryingapp.validation.implimentation.InventoryNumberForInventoryExistImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = InventoryNumberForInventoryExistImpl.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface InventoryNumberForInventoryExist {

    String message() default "Inventory number doesn't exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
