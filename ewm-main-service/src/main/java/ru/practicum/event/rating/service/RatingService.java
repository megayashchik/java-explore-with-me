package ru.practicum.event.rating.service;

public interface RatingService {
	void rateEvent(Long userId, Long eventId, String ratingType);

	long countLikes(Long eventId);

	long countDislikes(Long eventId);
}