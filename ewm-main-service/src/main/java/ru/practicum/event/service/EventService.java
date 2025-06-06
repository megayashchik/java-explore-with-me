package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.State;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
	EventFullDto createEventPrivate(Long userId, NewEventDto dto);

	EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest dto);

	EventFullDto findEventPublic(Long eventId, HttpServletRequest httpServletRequest);

	EventFullDto findEventPrivate(Long userId, Long eventId);

	List<ParticipationRequestDto> findEventRequestsPrivate(Long userId, Long eventId);

	EventRequestStatusUpdateResult updateEventRequestStatusPrivate(Long userId, Long eventId,
	                                                               EventRequestStatusUpdateRequest dto);

	EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto);

	List<EventShortDto> findEventsPublic(String text, List<Integer> categories,
	                                     Boolean paid, LocalDateTime rangeStart,
	                                     LocalDateTime rangeEnd, Boolean onlyAvailable,
	                                     String sort, Integer from, Integer size, HttpServletRequest httpServletRequest);

	List<EventShortDto> findEventsPrivate(Long userId, Integer from, Integer size);

	List<EventFullDto> findEventsAdmin(List<Integer> users, List<State> states,
	                                   List<Integer> categories,
	                                   LocalDateTime rangeStart,
	                                   LocalDateTime rangeEnd,
	                                   Integer from,
	                                   Integer size);
}