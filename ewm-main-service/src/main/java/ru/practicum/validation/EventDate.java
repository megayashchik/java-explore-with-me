package ru.practicum.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EventDateConstraintValidator.class)
@Documented
public @interface EventDate {
	String message() default "Event date must be in the future";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	int hours() default 2;
}