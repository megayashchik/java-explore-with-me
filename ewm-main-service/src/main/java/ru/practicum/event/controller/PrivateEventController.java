package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventController {
	private final EventService eventService;

	@GetMapping
	public List<EventShortDto> getUserEvents(
			@PathVariable Long userId,
			@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
			@Positive @RequestParam(defaultValue = "10") Integer size) {
		return eventService.getUserEvents(userId, from, size);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EventFullDto createEvent(@PathVariable Long userId,
	                                @Valid @RequestBody NewEventDto newEventDto) {
		return eventService.createEvent(userId, newEventDto);
	}

	@GetMapping("/{eventId}")
	public EventFullDto getUserEvent(@PathVariable Long userId,
	                                 @PathVariable Long eventId) {
		return eventService.getUserEvent(userId, eventId);
	}

	@PatchMapping("/{eventId}")
	public EventFullDto updateEvent(@PathVariable Long userId,
	                                @PathVariable Long eventId,
	                                @Valid @RequestBody UpdateEventUserRequest updateRequest) {
		return eventService.updateEventByUser(userId, eventId, updateRequest);
	}

	@GetMapping("/{eventId}/requests")
	public List<ParticipationRequestDto> getEventRequests(@PathVariable Long userId,
	                                                      @PathVariable Long eventId) {
		return eventService.getEventRequests(userId, eventId);
	}

	@PatchMapping("/{eventId}/requests")
	public EventRequestStatusUpdateResult updateRequestStatus(
			@PathVariable Long userId,
			@PathVariable Long eventId,
			@Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
		return eventService.updateRequestStatus(userId, eventId, updateRequest);
	}
}
