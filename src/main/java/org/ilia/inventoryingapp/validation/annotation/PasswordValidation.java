package org.ilia.inventoryingapp.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.inventoryingapp.validation.implimentation.PasswordValidationImpl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = PasswordValidationImpl.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface PasswordValidation {

    String message() default "The password  must not contain spaces";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
