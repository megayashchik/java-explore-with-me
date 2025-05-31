package ru.practicum.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class EventDateConstraintValidator implements ConstraintValidator<EventDate, LocalDateTime> {
	private int hours;

	@Override
	public void initialize(EventDate constraintAnnotation) {
		this.hours = constraintAnnotation.hours();
	}

	@Override
	public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
		if (value == null) {
			return true;
		}
		return value.isAfter(LocalDateTime.now().plusHours(hours));
	}
}