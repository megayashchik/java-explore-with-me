package ru.practicum.event.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String annotation;

	@ManyToOne
	@JoinColumn(name = "category_id")
	Category category;

	Long confirmedRequests;

	LocalDateTime createdOn;

	String description;

	LocalDateTime eventDate;

	@ManyToOne
	@JoinColumn(name = "initiator_id")
	User initiator;

	@ManyToOne
	@JoinColumn(name = "location_id")
	Location location;

	Boolean paid;

	Integer participantLimit;

	LocalDateTime publishedOn;

	Boolean requestModeration;

	@Enumerated(EnumType.STRING)
	State state;

	String title;

	Long views;
}