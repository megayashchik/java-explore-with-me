package ru.practicum.compilation.dto;

import lombok.Data;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;

@Data
public class CompilationDto {
	private Long id;
	private Set<EventShortDto> events;
	private Boolean pinned;
	private String title;
}
