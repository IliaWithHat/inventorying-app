package org.ilia.inventoryingapp.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.inventoryingapp.validation.implimentation.UniqueInventoryNumberForUserImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UniqueInventoryNumberForUserImpl.class)
@Target(TYPE)
@Retention(RUNTIME)
public @interface UniqueInventoryNumberForUser {

    String message() default "Inventory number exist";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
