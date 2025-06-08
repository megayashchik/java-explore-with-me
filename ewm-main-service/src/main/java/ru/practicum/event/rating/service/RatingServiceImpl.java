package ru.practicum.event.rating.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.rating.model.EventRating;
import ru.practicum.event.rating.model.Rating;
import ru.practicum.event.rating.repository.RatingRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.DuplicateRatingException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
	private final RatingRepository ratingRepository;
	private final EventRepository eventRepository;
	private final UserRepository userRepository;

	@Override
	@Transactional
	public void rateEvent(Long userId, Long eventId, String ratingType) {
		log.info("Пользователь с id = {} оценивает событие с id = {}, тип оценки: {}", userId, eventId, ratingType);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не существует"));
		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new NotFoundException("Событие с id = " + eventId + " не найдено"));

		Rating rating = Rating.valueOf(ratingType.toUpperCase());

		Optional<EventRating> existingRating = ratingRepository.findByUserAndEvent(user, event);

		if (existingRating.isPresent()) {
			EventRating eventRating = existingRating.get();
			if (eventRating.getRating() == rating) {
				throw new DuplicateRatingException("Вы уже поставили эту оценку данному событию");
			}

			Rating oldRating = eventRating.getRating();
			eventRating.setRating(rating);
			ratingRepository.save(eventRating);

			log.info("Обновлена существующая оценка пользователя с id = {} для события с id = {} с {} на {}",
					userId, eventId, oldRating, rating);
		} else {
			EventRating newRating = EventRating.builder()
					.user(user)
					.event(event)
					.rating(rating)
					.build();
			ratingRepository.save(newRating);

			log.info("Добавлена новая оценка {} для события с id = {} от пользователя с id = {}",
					rating, eventId, userId);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public long countLikes(Long eventId) {
		log.info("Подсчёт количества лайков для события с id = {}", eventId);

		Event event = eventRepository.getReferenceById(eventId);
		long likes = ratingRepository.countByEventAndRating(event, Rating.LIKE);
		log.info("Для события с id = {} поставлено {} лайков", eventId, likes);

		return likes;
	}

	@Override
	@Transactional(readOnly = true)
	public long countDislikes(Long eventId) {
		log.info("Подсчёт количества дизлайков для события с id = {}", eventId);

		Event event = eventRepository.getReferenceById(eventId);
		long dislikes = ratingRepository.countByEventAndRating(event, Rating.DISLIKE);
		log.info("Для события с id = {} поставлено {} дизлайков", eventId, dislikes);

		return dislikes;
	}
}