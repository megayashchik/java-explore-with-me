package ru.practicum.compilation.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@JoinTable(name = "compilations_events",
			joinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"))
	@ManyToMany
	List<Event> events;

	Boolean pinned;

	String title;
}