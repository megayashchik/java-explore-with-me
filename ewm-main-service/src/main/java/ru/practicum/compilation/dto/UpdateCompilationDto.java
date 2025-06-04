package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCompilationDto {
	Long id;

	List<Long> events;

	Boolean pinned = false;

	@Size(min = 2, max = 50)
	String title;
}