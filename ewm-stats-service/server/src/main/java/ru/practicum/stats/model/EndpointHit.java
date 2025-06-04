package ru.practicum.stats.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hits")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(nullable = false)
	String app;

	@Column(nullable = false)
	String uri;

	@Column(nullable = false)
	String ip;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime timestamp;
}