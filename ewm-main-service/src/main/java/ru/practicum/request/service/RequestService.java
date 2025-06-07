package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
	ParticipationRequestDto createUserRequestPrivate(Long userId, Long eventId);

	ParticipationRequestDto cancelUserRequestPrivate(Long userId, Long requestId);

	List<ParticipationRequestDto> findUserRequestsPrivate(Long userId);
}