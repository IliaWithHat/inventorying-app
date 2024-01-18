package org.ilia.inventoryingapp.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.inventoryingapp.validation.implimentation.UniqueInventoryNumberImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = UniqueInventoryNumberImpl.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface UniqueInventoryNumber {

    String message() default "You already created a record with this item";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
