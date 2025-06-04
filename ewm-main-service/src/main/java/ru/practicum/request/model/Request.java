package ru.practicum.request.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(nullable = false)
	LocalDateTime created;

	@ManyToOne
	@JoinColumn(name = "event")
	Event event;

	@ManyToOne
	@JoinColumn(name = "requester")
	User requester;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	RequestStatus status;
}