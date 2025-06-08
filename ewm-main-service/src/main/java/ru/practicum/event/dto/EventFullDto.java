package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.model.State;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
	Long id;

	String annotation;

	CategoryDto category;

	Long confirmedRequests;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdOn;

	String description;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime eventDate;

	UserShortDto initiator;

	LocationDto location;

	Boolean paid;

	Integer participantLimit;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime publishedOn;

	Boolean requestModeration;

	State state;

	String title;

	Long views;

	Long likes;

	Long dislikes;
}