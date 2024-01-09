package org.ilia.inventoryingapp.validation.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.ilia.inventoryingapp.validation.implimentation.UniqueEmailValidationImpl;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = UniqueEmailValidationImpl.class)
@Target(FIELD)
@Retention(RUNTIME)
public @interface UniqueEmail {

    String message() default "User with this email already exist!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
