package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {
	private final RequestService requestService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ParticipationRequestDto createUserRequestPrivate(@PathVariable Long userId,
	                                                        @RequestParam Long eventId) {
		return requestService.createUserRequestPrivate(userId, eventId);
	}

	@PatchMapping("/{requestId}/cancel")
	public ParticipationRequestDto cancelUserRequestPrivate(@PathVariable Long userId, @PathVariable Long requestId) {
		return requestService.cancelUserRequestPrivate(userId, requestId);
	}

	@GetMapping
	public List<ParticipationRequestDto> findUserRequestsPrivate(@PathVariable Long userId) {
		return requestService.findUserRequestsPrivate(userId);
	}
}