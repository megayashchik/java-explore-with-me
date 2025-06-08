package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.rating.service.RatingService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events/{eventId}/rating")
public class RatingController {
	private final RatingService ratingService;

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void rateEvent(
			@PathVariable Long eventId,
			@RequestParam String rating,
			@RequestHeader("X-User-Id") Long userId
	) {
		ratingService.rateEvent(userId, eventId, rating);
	}
}