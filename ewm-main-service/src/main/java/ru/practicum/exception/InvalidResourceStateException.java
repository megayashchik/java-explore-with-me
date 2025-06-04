package ru.practicum.exception;

public class InvalidResourceStateException extends RuntimeException {
	public InvalidResourceStateException(String message) {
		super(message);
	}
}