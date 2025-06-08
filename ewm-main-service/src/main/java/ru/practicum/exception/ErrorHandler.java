package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorResponse> handlerValidationException(ValidationException e) {
		return new ResponseEntity<>(new ErrorResponse("Ошибка валидации", e.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> handlerNotFoundException(NotFoundException e) {
		return new ResponseEntity<>(new ErrorResponse("Не найдено", e.getMessage()),
				HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
		return new ResponseEntity<>(new ErrorResponse("Ресурс уже существует", e.getMessage()),
				HttpStatus.CONFLICT);
	}

	@ExceptionHandler(InvalidResourceStateException.class)
	public ResponseEntity<ErrorResponse> handleInvalidResourceStateException(InvalidResourceStateException e) {
		return new ResponseEntity<>(new ErrorResponse("Недопустимое состояние ресурса", e.getMessage()),
				HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException e) {
		return new ResponseEntity<>(new ErrorResponse("Нарушение ограничения", e.getMessage()),
				HttpStatus.CONFLICT);
	}

	@ExceptionHandler(DuplicateRatingException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateRatingException(DuplicateRatingException e) {
		return new ResponseEntity<>(new ErrorResponse("Оценка уже существует", e.getMessage()),
				HttpStatus.CONFLICT);
	}
}