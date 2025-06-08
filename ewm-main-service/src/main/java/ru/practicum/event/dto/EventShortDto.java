package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventShortDto {
	Long id;

	String annotation;

	CategoryDto category;

	Long confirmedRequests;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime eventDate;

	UserShortDto initiator;

	Boolean paid;

	String title;

	Long views;

	Long likes;

	Long dislikes;
}