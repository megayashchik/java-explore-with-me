package ru.practicum.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findAllByInitiatorId(Long initiatorId, Pageable pageable);

	Optional<Event> findByIdAndInitiatorId(Long eventId, Long initiatorId);

	boolean existsByCategoryId(Long categoryId);

	@Query("SELECT e FROM Event e WHERE " +
			"e.state = ?1 AND " +
			"(?2 IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', ?2, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', ?2, '%'))) AND " +
			"(?3 IS NULL OR e.category.id IN ?3) AND " +
			"(?4 IS NULL OR e.paid = ?4) AND " +
			"e.eventDate >= ?5 AND " +
			"(?6 IS NULL OR e.eventDate <= ?6)")
	List<Event> findEventsPublic(EventState state, String text, List<Long> categories,
	                             Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
	                             Pageable pageable);

	@Query("SELECT e FROM Event e WHERE " +
			"(?1 IS NULL OR e.initiator.id IN ?1) AND " +
			"(?2 IS NULL OR e.state IN ?2) AND " +
			"(?3 IS NULL OR e.category.id IN ?3) AND " +
			"(?4 IS NULL OR e.eventDate >= ?4) AND " +
			"(?5 IS NULL OR e.eventDate <= ?5)")
	List<Event> findEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
	                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}