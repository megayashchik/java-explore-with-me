package ru.practicum.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class ErrorResponse {
	String error;

	@JsonProperty("message")
	String description;
}