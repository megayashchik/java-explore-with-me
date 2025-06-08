package ru.practicum.event.rating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.rating.model.EventRating;
import ru.practicum.event.rating.model.Rating;
import ru.practicum.user.model.User;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<EventRating, Long> {
	Optional<EventRating> findByUserAndEvent(User user, Event event);

	long countByEventAndRating(Event event, Rating rating);
}