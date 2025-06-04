package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.State;
import ru.practicum.event.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
	private final EventService eventService;

	@GetMapping
	public List<EventFullDto> findAdminEvents(@RequestParam(required = false) List<Integer> users,
	                                          @RequestParam(required = false) List<State> states,
	                                          @RequestParam(required = false) List<Integer> categories,
	                                          @RequestParam(required = false)
	                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	                                          LocalDateTime rangeStart,
	                                          @RequestParam(required = false)
	                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	                                          LocalDateTime rangeEnd,
	                                          @RequestParam(defaultValue = "0") Integer from,
	                                          @RequestParam(defaultValue = "10") Integer size) {
		return eventService.findAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
	}

	@PatchMapping("/{eventId}")
	public EventFullDto updateEventAdmin(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequest dto) {
		return eventService.updateEventAdmin(eventId, dto);
	}
}