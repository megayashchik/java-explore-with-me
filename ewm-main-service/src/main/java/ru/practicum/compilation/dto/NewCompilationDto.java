package ru.practicum.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class NewCompilationDto {
	private Set<Long> events;
	private Boolean pinned = false;

	@NotBlank(message = "Title cannot be blank")
	@Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
	private String title;
}