package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
	private final RequestRepository requestRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;

	public List<ParticipationRequestDto> getUserRequests(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new NotFoundException("User not found");
		}

		return requestRepository.findByRequesterId(userId).stream()
				.map(this::toDto)
				.collect(Collectors.toList());
	}

	@Transactional
	public ParticipationRequestDto createRequest(Long userId, Long eventId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("User not found"));

		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException("Event not found"));

		if (event.getInitiator().getId().equals(userId)) {
			throw new ConflictException("Event initiator cannot add request");
		}

		if (event.getState() != EventState.PUBLISHED) {
			throw new ConflictException("Event is not published");
		}

		if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
			throw new ConflictException("Request already exists");
		}

		if (event.getParticipantLimit() > 0 &&
				event.getConfirmedRequests() >= event.getParticipantLimit()) {
			throw new ConflictException("Event participant limit reached");
		}

		ParticipationRequest request = new ParticipationRequest();
		request.setCreated(LocalDateTime.now());
		request.setEvent(event);
		request.setRequester(user);
		request.setStatus(RequestStatus.PENDING);

		if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
			request.setStatus(RequestStatus.CONFIRMED);
			event.setConfirmedRequests(event.getConfirmedRequests() + 1);
			eventRepository.save(event);
		}

		return toDto(requestRepository.save(request));
	}

	@Transactional
	public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
		ParticipationRequest request = requestRepository.findByIdAndRequesterId(requestId, userId)
				.orElseThrow(() -> new NotFoundException("Request not found"));

		request.setStatus(RequestStatus.CANCELED);

		return toDto(requestRepository.save(request));
	}

	private ParticipationRequestDto toDto(ParticipationRequest request) {
		ParticipationRequestDto dto = new ParticipationRequestDto();
		dto.setId(request.getId());
		dto.setCreated(request.getCreated());
		dto.setEvent(request.getEvent().getId());
		dto.setRequester(request.getRequester().getId());
		dto.setStatus(request.getStatus().toString());
		return dto;
	}
}

