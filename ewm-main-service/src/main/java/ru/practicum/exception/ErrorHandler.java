package ru.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleNotFoundException(NotFoundException e) {
		log.error("Not found: {}", e.getMessage());
		return ApiError.builder()
				.status("NOT_FOUND")
				.reason("The required object was not found.")
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler(ConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleConflictException(ConflictException e) {
		log.error("Conflict: {}", e.getMessage());
		return ApiError.builder()
				.status("CONFLICT")
				.reason("For the requested operation the conditions are not met.")
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleValidationException(ValidationException e) {
		log.error("Validation error: {}", e.getMessage());
		return ApiError.builder()
				.status("BAD_REQUEST")
				.reason("Incorrectly made request.")
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ApiError handleForbiddenException(ForbiddenException e) {
		log.error("Forbidden: {}", e.getMessage());
		return ApiError.builder()
				.status("FORBIDDEN")
				.reason("For the requested operation the conditions are not met.")
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.joining(", "));
		log.error("Validation error: {}", message);
		return ApiError.builder()
				.status("BAD_REQUEST")
				.reason("Incorrectly made request.")
				.message(message)
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler({
			ConstraintViolationException.class,
			MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleValidationExceptions(Exception e) {
		log.error("Validation error: {}", e.getMessage());
		return ApiError.builder()
				.status("BAD_REQUEST")
				.reason("Incorrectly made request.")
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleDataIntegrityViolation(DataIntegrityViolationException e) {
		log.error("Data integrity violation: {}", e.getMessage());
		return ApiError.builder()
				.status("CONFLICT")
				.reason("Integrity constraint has been violated.")
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ApiError handleGenericException(Exception e) {
		log.error("Internal server error: {}", e.getMessage(), e);
		return ApiError.builder()
				.status("INTERNAL_SERVER_ERROR")
				.reason("Error occurred.")
				.message(e.getMessage())
				.timestamp(LocalDateTime.now())
				.build();
	}
}
