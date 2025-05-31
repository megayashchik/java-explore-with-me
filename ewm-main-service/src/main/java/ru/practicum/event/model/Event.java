package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;


@Entity
@Table(name = "events")
@Getter
@Setter
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String annotation;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(name = "confirmed_requests")
	private Long confirmedRequests = 0L;

	@Column(name = "created_on")
	private LocalDateTime createdOn;

	private String description;

	@Column(name = "event_date")
	private LocalDateTime eventDate;

	@ManyToOne
	@JoinColumn(name = "initiator_id")
	private User initiator;

	private Float lat;
	private Float lon;

	private Boolean paid = false;

	@Column(name = "participant_limit")
	private Integer participantLimit = 0;

	@Column(name = "published_on")
	private LocalDateTime publishedOn;

	@Column(name = "request_moderation")
	private Boolean requestModeration = true;

	@Enumerated(EnumType.STRING)
	private EventState state;

	private String title;

	@Transient
	private Long views = 0L;
}
