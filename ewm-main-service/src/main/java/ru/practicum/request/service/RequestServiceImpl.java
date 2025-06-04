package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.State;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
	private final RequestRepository requestRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;
	private final RequestMapper requestMapper;

	@Override
	public ParticipationRequestDto createUserRequestPrivate(Long userId, Long eventId) {
		log.info("Создание запроса на участие для пользователя с id = {} в событии с id = {}", userId, eventId);

		User requester = userRepository.findById(userId).orElseThrow(() ->
				new NotFoundException("Пользователь с id = " + userId + " не существует"));

		Event event = eventRepository.findById(eventId).orElseThrow(() ->
				new NotFoundException("Событие с id = " + eventId + " не существует"));

		if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
			throw new ResourceAlreadyExistsException("Нельзя добавить повторный запрос");
		}

		if (Objects.equals(event.getInitiator().getId(), userId)) {
			throw new InvalidResourceStateException("Нельзя добавить запрос на участие в своем событии");
		}

		if (event.getState() != State.PUBLISHED) {
			throw new InvalidResourceStateException("Нельзя участвовать в неопубликованном событии");
		}

		if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
			throw new ConstraintViolationException("Достигнут лимит запросов на участие");
		}

		Request request = new Request();

		if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
			request.setStatus(RequestStatus.PENDING);
		} else {
			request.setStatus(RequestStatus.CONFIRMED);
		}

		request.setCreated(LocalDateTime.now());
		request.setEvent(event);
		request.setRequester(requester);

		Request savedRequest = requestRepository.save(request);

		if (request.getStatus() == RequestStatus.CONFIRMED) {
			event.setConfirmedRequests(event.getConfirmedRequests() + 1);
			eventRepository.save(event);
		}
		log.info("Запрос на участие с id = {} для пользователя с id = {} в событии с id = {} " +
						"успешно создан со статусом {}",
				savedRequest.getId(), userId, eventId, savedRequest.getStatus());

		return requestMapper.toDto(savedRequest);
	}

	@Override
	public ParticipationRequestDto cancelUserRequestPrivate(Long userId, Long requestId) {
		log.info("Отмена запроса на участие с id = {} пользователем с id = {}", requestId, userId);

		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("Пользователь с id = " + userId + " не существует");
		}

		Request request = requestRepository.findById(requestId).orElseThrow(() ->
				new NotFoundException("Запрос с id = " + requestId + " не существует"));

		if (!Objects.equals(request.getRequester().getId(), userId)) {
			throw new ValidationException("Пользователь с id = " + userId + " не создавал запрос " + requestId);
		}

		request.setStatus(RequestStatus.CANCELED);
		log.info("Запрос на участие с id = {} успешно отменен пользователем с id = {}", requestId, userId);

		return requestMapper.toDto(requestRepository.save(request));
	}

	@Override
	public List<ParticipationRequestDto> findUserRequestsPrivate(Long userId) {
		log.info("Поиск запросов на участие для пользователя с id = {}", userId);

		List<Request> requests = requestRepository.findAllByRequesterId(userId);
		log.info("Найдено {} запросов на участие для пользователя с id = {}", requests.size(), userId);

		return requests.stream()
				.map(requestMapper::toDto)
				.toList();
	}
}