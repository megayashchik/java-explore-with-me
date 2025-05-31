package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

	List<ParticipationRequest> findByRequesterId(Long requesterId);

	List<ParticipationRequest> findByEventId(Long eventId);

	Optional<ParticipationRequest> findByIdAndRequesterId(Long id, Long requesterId);

	boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);
}