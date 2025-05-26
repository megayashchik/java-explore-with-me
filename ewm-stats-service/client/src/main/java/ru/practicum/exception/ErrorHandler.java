package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleNotFoundException(NotFoundException e, WebRequest request) {
		log.error("Ресурс не найден: {}", e.getMessage());
		return new ErrorResponse("Ресурс не найден: " + e.getMessage());
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleValidationException(ValidationException e, WebRequest request) {
		log.error("Ошибка валидации: {}", e.getMessage());
		return new ErrorResponse("Ошибка валидации данных: " + e.getMessage());
	}

	@ExceptionHandler(InternalErrorException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleInternalError(InternalErrorException e, WebRequest request) {
		log.error("Внутренняя ошибка сервера: {}", e.getMessage());
		return new ErrorResponse("Произошла внутренняя ошибка сервера: " + e.getMessage());
	}

	@ExceptionHandler(Throwable.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleThrowable(final Throwable e) {
		log.error("Непредвиденная ошибка: {}", e.getMessage());
		return new ErrorResponse("Произошла непредвиденная ошибка: " + e.getMessage());
	}
}