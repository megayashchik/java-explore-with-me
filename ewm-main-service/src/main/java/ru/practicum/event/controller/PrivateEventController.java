package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {
	private final EventService eventService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EventFullDto createEventPrivate(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
		return eventService.createEventPrivate(userId, dto);
	}

	@PatchMapping("/{eventId}")
	public EventFullDto updateEventPrivate(@PathVariable Long userId,
	                                       @PathVariable Long eventId,
	                                       @Valid @RequestBody UpdateEventUserRequest dto) {
		return eventService.updateEventPrivate(userId, eventId, dto);
	}

	@GetMapping
	public List<EventShortDto> findEventsPrivate(@PathVariable Long userId,
	                                             @RequestParam(defaultValue = "0") Integer from,
	                                             @RequestParam(defaultValue = "10") Integer size) {
		return eventService.findEventsPrivate(userId, from, size);
	}

	@GetMapping("/{eventId}")
	public EventFullDto findEventPrivate(@PathVariable Long userId, @PathVariable Long eventId) {
		return eventService.findEventPrivate(userId, eventId);
	}

	@GetMapping("/{eventId}/requests")
	public List<ParticipationRequestDto> findEventRequestsPrivate(@PathVariable Long userId,
	                                                              @PathVariable Long eventId) {
		return eventService.findEventRequestsPrivate(userId, eventId);
	}

	@PatchMapping("/{eventId}/requests")
	public EventRequestStatusUpdateResult updateEventRequestStatusPrivate(@PathVariable Long userId,
			@PathVariable Long eventId,
			@RequestBody EventRequestStatusUpdateRequest dto) {
		return eventService.updateEventRequestStatusPrivate(userId, eventId, dto);
	}
}