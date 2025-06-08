package ru.practicum.exception;

public class DuplicateRatingException extends RuntimeException {
	public DuplicateRatingException(String message) {
		super(message);
	}
}