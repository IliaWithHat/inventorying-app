package org.ilia.inventoryingapp.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.inventoryingapp.validation.implimentation.ValidateAdditionalInfoImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = ValidateAdditionalInfoImpl.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface ValidateAdditionalInfo {

    String message() default "Check the correct spelling of additional info";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
