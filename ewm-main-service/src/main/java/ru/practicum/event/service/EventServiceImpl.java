package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitRequest;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsResponse;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.mapper.LocationMapper;
import ru.practicum.event.mapper.UpdateEventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventSort;
import ru.practicum.event.model.Location;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.event.service.specification.DbSpecifications;
import ru.practicum.exception.ConstraintViolationException;
import ru.practicum.exception.InvalidResourceStateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
	private final RequestMapper requestMapper;
	private final RequestRepository requestRepository;
	private final EventRepository eventRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final LocationRepository locationRepository;
	private final EventMapper eventMapper;
	private final LocationMapper locationMapper;
	private final StatsClient statsClient;

	@Override
	public List<EventShortDto> findEventsPrivate(Long userId, Integer from, Integer size) {
		log.info("Получение событий пользователя id = {}, from = {}, size = {}", userId, from, size);

		Pageable pageable = PageRequest.of(from / size, size);
		List<Event> events;

		events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();
		log.info("Найдено {} событий пользователя id = {}", events.size(), userId);

		return events.stream()
				.map(eventMapper::toShortDto)
				.toList();
	}

	@Override
	public List<EventShortDto> findEventsPublic(String text, List<Integer> categories, Boolean paid,
	                                            LocalDateTime rangeStart, LocalDateTime rangeEnd,
	                                            Boolean onlyAvailable, String sort, Integer from, Integer size,
	                                            HttpServletRequest httpServletRequest) {
		log.info("Публичный поиск событий: text = '{}', categories = {}, paid = {}, rangeStart = {}, rangeEnd = {}, " +
						"onlyAvailable = {}, sort = {}, from = {}, size = {}",
				text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

		if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
			throw new ValidationException("Время начала позже времени окончания");
		}

		Specification<Event> spec = DbSpecifications.getSpecificationPublic(text, categories, paid, rangeStart,
				rangeEnd, onlyAvailable);

		EventSort eventSort = sort != null ? EventSort.valueOf(sort.toUpperCase()) : null;
		Sort sorting = Sort.unsorted();
		if (eventSort != null) {
			if (eventSort == EventSort.EVENT_DATE) {
				sorting = Sort.by(Sort.Direction.DESC, "eventDate");
			} else if (eventSort == EventSort.VIEWS) {
				sorting = Sort.by(Sort.Direction.DESC, "views");
			}
		}

		hit(httpServletRequest);

		Pageable pageable = PageRequest.of(from / size, size, sorting);
		List<Event> events = eventRepository.findAll(spec, pageable).getContent();
		log.info("Найдено {} событий по публичному запросу", events.size());

		return eventMapper.toShortDto(events);

	}

	@Override
	public List<EventFullDto> findAdminEvents(List<Integer> users, List<State> states, List<Integer> categories,
	                                          LocalDateTime rangeStart, LocalDateTime rangeEnd,
	                                          Integer from, Integer size) {
		log.info("Административный поиск событий: users = {}, states = {}, categories = {}, " +
						"rangeStart = {}, rangeEnd = {}, from = {}, size = {}",
				users, states, categories, rangeStart, rangeEnd, from, size);

		Specification<Event> spec = DbSpecifications.getSpecificationAdmin(
				users, states, categories, rangeStart, rangeEnd);

		Pageable pageable = PageRequest.of(from / size, size);
		List<Event> events = eventRepository.findAll(spec, pageable).getContent();
		log.info("Найдено {} событий по административному запросу", events.size());

		return events.stream()
				.map(eventMapper::toFullDto)
				.toList();
	}

	@Override
	public EventFullDto findEventByIdPublic(Long eventId, HttpServletRequest httpServletRequest) {
		log.info("Публичный запрос события с id = {}", eventId);

		Event event = eventRepository.findById(eventId).orElseThrow(() ->
				new NotFoundException("Событие с id = " + eventId + " не найдено"));

		if (event.getState() != State.PUBLISHED) {
			throw new NotFoundException("Событие с id = " + eventId + " не опубликовано");
		}

		hit(httpServletRequest);

		List<ViewStatsResponse> stats = statsClient.findStats(event.getPublishedOn(),
				LocalDateTime.now().plusMinutes(1), List.of("/events/" + eventId), true);
		log.info("Получена статистика просмотров события с id = {}: {} записей", eventId, stats.size());

		Long views = stats.isEmpty() ? 0L : stats.getFirst().getHits();
		event.setViews(views);
		log.info("Установлено количество просмотров для события с id = {}: {}", eventId, views);

		return eventMapper.toFullDto(event);
	}

	@Override
	public EventFullDto createEventPrivate(Long userId, NewEventDto dto) {
		log.info("Создание события: {} от пользователя с id = {}", dto.getTitle(), userId);

		if (LocalDateTime.parse(dto.getEventDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
				.isBefore(LocalDateTime.now())) {
			throw new ValidationException("Указана неверная дата события");
		}

		Category category = categoryRepository.findById(dto.getCategory())
				.orElseThrow(() -> new NotFoundException("Категория " + dto.getCategory() + " не найдена"));

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

		Location location = locationRepository.save(locationMapper.toEntity(dto.getLocation()));

		Event entity = eventMapper.toEntity(dto, category, user);
		entity.setCreatedOn(LocalDateTime.now());
		entity.setConfirmedRequests(0L);
		entity.setLocation(location);
		entity.setState(State.PENDING);

		Event savedEvent = eventRepository.save(entity);
		log.info("Создано событие {}", savedEvent.getId());

		return eventMapper.toFullDto(savedEvent);
	}

	@Override
	public EventFullDto findEventByUserPrivate(Long userId, Long eventId) {
		log.info("Запрос события с id = {} пользователем с id = {}", eventId, userId);

		Optional<Event> event = eventRepository.findByInitiatorIdAndId(userId, eventId);

		if (event.isEmpty()) {
			throw new NotFoundException("Событие с id = " + eventId + " не найдено");
		}
		log.info("Успешно найдено событие id = {} для пользователя id = {}", eventId, userId);

		return eventMapper.toFullDto(event.get());
	}

	@Override
	public EventFullDto updateEventPrivate(Long userId, Long eventId, UpdateEventUserRequest dto) {
		log.info("Private: Обновление события id = {} пользователем id = {}", eventId, userId);

		Event event = eventRepository.findById(eventId).orElseThrow(() ->
				new NotFoundException("Событие с id = " + eventId + " не найдено."));

		checkEventUpdatePrivate(event, userId, eventId);

		event = UpdateEventMapper.updateEventPrivate(event, dto, categoryRepository,
				locationRepository, locationMapper);

		if (dto.getStateAction() != null) {
			switch (dto.getStateAction()) {
				case CANCEL_REVIEW -> event.setState(State.CANCELED);
				case REJECT_EVENT -> event.setState(State.REJECT);
				case SEND_TO_REVIEW -> event.setState(State.PENDING);
				case PUBLISH_EVENT -> event.setState(State.PUBLISHED);
			}
		}

		Event savedEvent = eventRepository.save(event);
		log.info("Событие с id = {} успешно обновлено, текущий статус: {}", eventId, savedEvent.getState());

		return eventMapper.toFullDto(eventRepository.save(event));
	}

	@Override
	public List<ParticipationRequestDto> findEventRequestsPrivate(Long userId, Long eventId) {
		log.info("Запрос заявок на участие в событии с id = {} пользователем с id = {}", eventId, userId);

		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id = " + userId + " не существует");
		}

		Event event = eventRepository.findById(eventId).orElseThrow(() ->
				new NotFoundException("Событие с id = " + eventId + " не найдено."));

		if (!Objects.equals(event.getInitiator().getId(), userId)) {
			throw new ValidationException("Пользователь с id = " + userId +
					" не является создателем события с id = " + eventId);
		}

		List<Request> requests = requestRepository.findAllByEventId(eventId);
		log.info("Найдено {} заявок для события с id = {}", requests.size(), eventId);

		return requests.stream()
				.map(requestMapper::toDto)
				.toList();
	}

	@Override
	public EventRequestStatusUpdateResult updateEventRequestPrivate(Long userId, Long eventId,
	                                                                EventRequestStatusUpdateRequest dto) {
		log.info("Обновление статуса заявок для события с id = {} пользователем с id = {}, статус = {}, заявки: {}",
				eventId, userId, dto.getStatus(), dto.getRequestIds());

		Event event = eventRepository.findById(eventId).orElseThrow(() ->
				new NotFoundException("Событие с id = " + eventId + " не найдено."));

		checkUpdateEventRequestPrivate(event, userId, eventId, dto);

		List<Request> requests = requestRepository.findAllById(dto.getRequestIds());
		List<Request> confirmedRequests = new ArrayList<>();
		List<Request> rejectedRequests = new ArrayList<>();

		requests.forEach(request -> {
			if (request.getStatus() != RequestStatus.PENDING) {
				throw new InvalidResourceStateException("Статус заявки не в состоянии ожидания");
			}

			if (event.getConfirmedRequests() < event.getParticipantLimit() && dto.getStatus() == RequestStatus.CONFIRMED) {
				request.setStatus(RequestStatus.CONFIRMED);
				confirmedRequests.add(request);
				event.setConfirmedRequests(event.getConfirmedRequests() + 1);
			} else {
				request.setStatus(RequestStatus.REJECTED);
				rejectedRequests.add(request);
			}
		});

		eventRepository.save(event);
		requestRepository.saveAll(requests);

		List<ParticipationRequestDto> confirmedDtoList = confirmedRequests.stream()
				.map(requestMapper::toDto)
				.toList();

		List<ParticipationRequestDto> rejectedDtoList = rejectedRequests.stream()
				.map(requestMapper::toDto)
				.toList();

		log.info("Результат обновления для события с id = {}: подтверждено: {}, отклонено: {}",
				eventId, confirmedDtoList.size(), rejectedDtoList.size());

		return new EventRequestStatusUpdateResult(confirmedDtoList, rejectedDtoList);
	}

	@Override
	public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest dto) {
		log.info("Admin: Обновление события с id = {}", eventId);

		Event event = eventRepository.findById(eventId).orElseThrow(() ->
				new NotFoundException("Событие с id = " + eventId + " не найдено."));

		event = UpdateEventMapper.updateEventAdmin(event, dto, categoryRepository, locationRepository, locationMapper);
		log.info("Событие id = {} успешно обновлено администратором", eventId);

		return eventMapper.toFullDto(eventRepository.save(event));
	}

	private void hit(HttpServletRequest request) {
		String uri = request.getRequestURI();

		EndpointHitRequest hitDto = new EndpointHitRequest(
				"ewm-service",
				uri,
				request.getRemoteAddr(),
				LocalDateTime.now()
		);

		log.info("Сохранение данных о просмотре: app={}, uri={}, ip={}",
				"ewm-service", uri, request.getRemoteAddr());

		statsClient.hit(hitDto);
	}


	private void checkEventUpdatePrivate(Event event, Long userId, Long eventId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id = " + userId + " не существует");
		}

		if (!Objects.equals(event.getInitiator().getId(), userId)) {
			throw new ValidationException("Пользователь с id = " + userId + " не является создателем события " + eventId);
		}

		if (event.getState() == State.PUBLISHED) {
			throw new InvalidResourceStateException("Событие не отменено и не в состоянии ожидания.");
		}

		if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
			throw new ConstraintViolationException("Время события указано раньше, " +
					"чем через два часа от текущего момента");
		}
	}

	private void checkUpdateEventRequestPrivate(Event event, Long userId, Long eventId,
	                                            EventRequestStatusUpdateRequest dto) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id = " + userId + " не существует");
		}

		if (!Objects.equals(event.getInitiator().getId(), userId)) {
			throw new ValidationException("Пользователь с id = " + userId +
					" не является создателем события " + eventId);
		}

		if (event.getState() != State.PUBLISHED) {
			throw new InvalidResourceStateException("Событие не опубликовано");
		}

		if (event.getConfirmedRequests() != null) {
			if (RequestStatus.CONFIRMED.equals(dto.getStatus())
					&& event.getConfirmedRequests() >= event.getParticipantLimit()) {
				throw new ConstraintViolationException("Достигнут лимит заявок");
			}
		}
	}
}
