package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {
	private final EventService eventService;
	private final StatsClient statsClient;

	@GetMapping
	public List<EventShortDto> getEvents(
			@RequestParam(required = false) String text,
			@RequestParam(required = false) List<Long> categories,
			@RequestParam(required = false) Boolean paid,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
			@RequestParam(defaultValue = "false") Boolean onlyAvailable,
			@RequestParam(required = false) String sort,
			@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
			@Positive @RequestParam(defaultValue = "10") Integer size,
			HttpServletRequest request) {

		// Отправка статистики
		statsClient.hit("ewm-main-service", request.getRequestURI(),
				request.getRemoteAddr(), LocalDateTime.now());

		return eventService.getPublicEvents(text, categories, paid, rangeStart,
				rangeEnd, onlyAvailable, sort, from, size);
	}

	@GetMapping("/{id}")
	public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
		// Отправка статистики
		statsClient.hit("ewm-main-service", request.getRequestURI(),
				request.getRemoteAddr(), LocalDateTime.now());

		return eventService.getPublicEvent(id);
	}
}
