package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final RequestRepository requestRepository;
	private final StatsClient statsClient;

	@Transactional
	public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
		if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
			throw new ConflictException("Event date must be at least 2 hours from now");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		Category category = categoryRepository.findById(newEventDto.getCategory())
				.orElseThrow(() -> new NotFoundException("Category not found"));

		Event event = new Event();
		event.setAnnotation(newEventDto.getAnnotation());
		event.setCategory(category);
		event.setDescription(newEventDto.getDescription());
		event.setEventDate(newEventDto.getEventDate());
		event.setInitiator(user);
		event.setLat(newEventDto.getLocation().getLat());
		event.setLon(newEventDto.getLocation().getLon());
		event.setPaid(newEventDto.getPaid() != null ? newEventDto.getPaid() : false);
		event.setParticipantLimit(newEventDto.getParticipantLimit() != null ? newEventDto.getParticipantLimit() : 0);
		event.setRequestModeration(newEventDto.getRequestModeration() != null ? newEventDto.getRequestModeration() : true);
		event.setTitle(newEventDto.getTitle());
		event.setState(EventState.PENDING);
		event.setCreatedOn(LocalDateTime.now());
		event.setConfirmedRequests(0L);

		return toEventFullDto(eventRepository.save(event));
	}

	public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
		userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		PageRequest page = PageRequest.of(from / size, size);
		List<Event> events = eventRepository.findAllByInitiatorId(userId, page);

		return events.stream()
				.map(this::toEventShortDto)
				.collect(Collectors.toList());
	}

	public EventFullDto getUserEvent(Long userId, Long eventId) {
		Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
				.orElseThrow(() -> new NotFoundException("Event not found"));

		return toEventFullDto(event);
	}

	@Transactional
	public EventFullDto updateEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
		Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
				.orElseThrow(() -> new NotFoundException("Event not found"));

		if (event.getState() == EventState.PUBLISHED) {
			throw new ConflictException("Only pending or canceled events can be changed");
		}

		if (updateRequest.getEventDate() != null &&
				updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
			throw new ConflictException("Event date must be at least 2 hours from now");
		}

		updateEvent(event, updateRequest);

		if (updateRequest.getStateAction() != null) {
			if (updateRequest.getStateAction() == UpdateEventUserRequest.StateAction.SEND_TO_REVIEW) {
				event.setState(EventState.PENDING);
			} else if (updateRequest.getStateAction() == UpdateEventUserRequest.StateAction.CANCEL_REVIEW) {
				event.setState(EventState.CANCELED);
			}
		}

		return toEventFullDto(eventRepository.save(event));
	}

	public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
	                                           LocalDateTime rangeStart, LocalDateTime rangeEnd,
	                                           Boolean onlyAvailable, String sort,
	                                           Integer from, Integer size) {
		if (rangeStart == null) {
			rangeStart = LocalDateTime.now();
		}

		PageRequest page = PageRequest.of(from / size, size);
		if ("EVENT_DATE".equals(sort)) {
			page = PageRequest.of(from / size, size, Sort.by("eventDate"));
		}

		List<Event> events = eventRepository.findEventsPublic(EventState.PUBLISHED, text, categories,
				paid, rangeStart, rangeEnd, page);

		if (onlyAvailable) {
			events = events.stream()
					.filter(e -> e.getParticipantLimit() == 0 ||
							e.getConfirmedRequests() < e.getParticipantLimit())
					.collect(Collectors.toList());
		}

		// Добавляем просмотры
		addViewsToEvents(events);

		if ("VIEWS".equals(sort)) {
			events.sort((e1, e2) -> e2.getViews().compareTo(e1.getViews()));
		}

		return events.stream()
				.map(this::toEventShortDto)
				.collect(Collectors.toList());
	}

	public EventFullDto getPublicEvent(Long eventId) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException("Event not found"));

		if (event.getState() != EventState.PUBLISHED) {
			throw new NotFoundException("Event not found");
		}

		addViewsToEvents(List.of(event));
		return toEventFullDto(event);
	}

	public List<EventFullDto> getAdminEvents(List<Long> users, List<String> states,
	                                         List<Long> categories, LocalDateTime rangeStart,
	                                         LocalDateTime rangeEnd, Integer from, Integer size) {
		List<EventState> eventStates = null;
		if (states != null) {
			eventStates = states.stream()
					.map(EventState::valueOf)
					.collect(Collectors.toList());
		}

		PageRequest page = PageRequest.of(from / size, size);
		List<Event> events = eventRepository.findEventsAdmin(users, eventStates, categories,
				rangeStart, rangeEnd, page);

		return events.stream()
				.map(this::toEventFullDto)
				.collect(Collectors.toList());
	}

	@Transactional
	public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException("Event not found"));

		if (updateRequest.getEventDate() != null &&
				updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
			throw new ConflictException("Event date must be at least 1 hour from now");
		}

		updateEvent(event, updateRequest);

		if (updateRequest.getStateAction() != null) {
			if (updateRequest.getStateAction() == UpdateEventAdminRequest.StateAction.PUBLISH_EVENT) {
				if (event.getState() != EventState.PENDING) {
					throw new ConflictException("Cannot publish the event because it's not in the right state: " + event.getState());
				}
				event.setState(EventState.PUBLISHED);
				event.setPublishedOn(LocalDateTime.now());
			} else if (updateRequest.getStateAction() == UpdateEventAdminRequest.StateAction.REJECT_EVENT) {
				if (event.getState() == EventState.PUBLISHED) {
					throw new ConflictException("Cannot reject the event because it's not in the right state: PUBLISHED");
				}
				event.setState(EventState.CANCELED);
			}
		}

		return toEventFullDto(eventRepository.save(event));
	}

	public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
		eventRepository.findByIdAndInitiatorId(eventId, userId)
				.orElseThrow(() -> new NotFoundException("Event not found"));

		return requestRepository.findByEventId(eventId).stream()
				.map(this::toRequestDto)
				.collect(Collectors.toList());
	}

	@Transactional
	public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
	                                                          EventRequestStatusUpdateRequest updateRequest) {
		Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
				.orElseThrow(() -> new NotFoundException("Event not found"));

		List<ParticipationRequest> requests = requestRepository.findAllById(updateRequest.getRequestIds());

		EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
		result.setConfirmedRequests(new ArrayList<>());
		result.setRejectedRequests(new ArrayList<>());

		for (ParticipationRequest request : requests) {
			if (request.getStatus() != RequestStatus.PENDING) {
				throw new ConflictException("Request must have status PENDING");
			}

			if (updateRequest.getStatus() == RequestStatus.CONFIRMED) {
				if (event.getParticipantLimit() > 0 &&
						event.getConfirmedRequests() >= event.getParticipantLimit()) {
					request.setStatus(RequestStatus.REJECTED);
					result.getRejectedRequests().add(toRequestDto(request));
				} else {
					request.setStatus(RequestStatus.CONFIRMED);
					event.setConfirmedRequests(event.getConfirmedRequests() + 1);
					result.getConfirmedRequests().add(toRequestDto(request));
				}
			} else {
				request.setStatus(RequestStatus.REJECTED);
				result.getRejectedRequests().add(toRequestDto(request));
			}
		}

		requestRepository.saveAll(requests);
		eventRepository.save(event);

		return result;
	}

	private void updateEvent(Event event, Object updateRequest) {
		if (updateRequest instanceof UpdateEventUserRequest) {
			UpdateEventUserRequest req = (UpdateEventUserRequest) updateRequest;
			if (req.getAnnotation() != null) event.setAnnotation(req.getAnnotation());
			if (req.getCategory() != null) {
				event.setCategory(categoryRepository.findById(req.getCategory())
						.orElseThrow(() -> new NotFoundException("Category not found")));
			}
			if (req.getDescription() != null) event.setDescription(req.getDescription());
			if (req.getEventDate() != null) event.setEventDate(req.getEventDate());
			if (req.getLocation() != null) {
				event.setLat(req.getLocation().getLat());
				event.setLon(req.getLocation().getLon());
			}
			if (req.getPaid() != null) event.setPaid(req.getPaid());
			if (req.getParticipantLimit() != null) event.setParticipantLimit(req.getParticipantLimit());
			if (req.getRequestModeration() != null) event.setRequestModeration(req.getRequestModeration());
			if (req.getTitle() != null) event.setTitle(req.getTitle());
		}
		// Аналогично для UpdateEventAdminRequest
	}

	private void addViewsToEvents(List<Event> events) {
		try {
			List<String> uris = events.stream()
					.map(e -> "/events/" + e.getId())
					.collect(Collectors.toList());

			LocalDateTime start = LocalDateTime.now().minusYears(10);
			List<?> stats = statsClient.getStats(start, LocalDateTime.now(), uris, true);

			// Простая обработка статистики
			events.forEach(event -> event.setViews(0L)); // По умолчанию 0 просмотров

		} catch (Exception e) {
			log.error("Stats service error: {}", e.getMessage());
			events.forEach(event -> event.setViews(0L));
		}
	}
}

